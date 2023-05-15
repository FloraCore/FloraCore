package team.floracore.common.locale.translation;

import com.google.gson.*;
import okhttp3.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.config.*;
import team.floracore.common.http.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.gson.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class TranslationRepository {
    private static final String TRANSLATIONS_INFO_ENDPOINT = "https://fc-meta.kinomc.net/data/translations";
    private static final String TRANSLATIONS_DOWNLOAD_ENDPOINT = "https://fc-meta.kinomc.net/translation/";
    private static final long MAX_BUNDLE_SIZE = 1048576L; // 1mb
    private static final long CACHE_MAX_AGE = TimeUnit.HOURS.toMillis(1);

    private final FloraCorePlugin plugin;
    private final AbstractHttpClient abstractHttpClient;

    public TranslationRepository(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.abstractHttpClient = new AbstractHttpClient(plugin.getHttpClient());
    }

    /**
     * Gets a list of available languages.
     *
     * @return a list of languages
     * @throws java.io.IOException          if an i/o error occurs
     * @throws UnsuccessfulRequestException if the http request fails
     */
    public List<LanguageInfo> getAvailableLanguages() throws IOException, UnsuccessfulRequestException {
        return getTranslationsMetadata().languages;
    }

    public void scheduleRefreshRepeating() {
        this.plugin.getBootstrap().getScheduler().asyncRepeating(this::scheduleRefresh, 1, TimeUnit.HOURS);
    }

    /**
     * Schedules a refresh of the current translations if necessary.
     */
    public void scheduleRefresh() {
        if (!this.plugin.getConfiguration().get(ConfigKeys.AUTO_INSTALL_TRANSLATIONS)) {
            return; // skip
        }

        this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
            // cleanup old translation files
            clearDirectory(this.plugin.getTranslationManager().getTranslationsDirectory(), Files::isRegularFile);

            try {
                refresh();
            } catch (Exception e) {
                // ignore
            }
        });
    }

    private void refresh() throws Exception {
        long lastRefresh = readLastRefreshTime();
        long timeSinceLastRefresh = System.currentTimeMillis() - lastRefresh;

        if (timeSinceLastRefresh <= CACHE_MAX_AGE) {
            return;
        }

        MetadataResponse metadata = getTranslationsMetadata();

        // perform a refresh!
        downloadAndInstallTranslations(metadata.languages, null, true);
    }

    private void clearDirectory(Path directory, Predicate<Path> predicate) {
        try {
            Files.list(directory).filter(predicate).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // ignore
                }
            });
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * Downloads and installs translations for the given languages.
     *
     * @param languages    the languages to install translations for
     * @param sender       the sender to report progress to
     * @param updateStatus if the status file should be updated
     */
    public void downloadAndInstallTranslations(List<LanguageInfo> languages, @Nullable Sender sender, boolean updateStatus) {
        TranslationManager manager = this.plugin.getTranslationManager();
        Path translationsDirectory = manager.getRepositoryTranslationsDirectory();

        // clear existing translations
        clearDirectory(translationsDirectory, TranslationManager::isTranslationFile);

        for (LanguageInfo language : languages) {
            if (sender != null) {
                MiscMessage.TRANSLATIONS_INSTALLING_SPECIFIC.send(sender, language.locale().toString());
            }

            Request request = new Request.Builder().header("User-Agent", "floracore").url(TRANSLATIONS_DOWNLOAD_ENDPOINT + language.id()).build();

            Path file = translationsDirectory.resolve(language.locale().toString() + ".properties");

            try (Response response = abstractHttpClient.makeHttpRequest(request)) {
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        throw new IOException("No response");
                    }

                    try (InputStream inputStream = new LimitedInputStream(responseBody.byteStream(), MAX_BUNDLE_SIZE)) {
                        Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (UnsuccessfulRequestException | IOException e) {
                if (sender != null) {
                    MiscMessage.TRANSLATIONS_DOWNLOAD_ERROR.send(sender, language.locale().toString());
                    this.plugin.getLogger().warn("Unable to download translations", e);
                }
            }
        }

        if (updateStatus) {
            writeLastRefreshTime();
        }

        manager.reload();
    }

    private void writeLastRefreshTime() {
        Path statusFile = this.plugin.getTranslationManager().getRepositoryStatusFile();

        try (BufferedWriter writer = Files.newBufferedWriter(statusFile, StandardCharsets.UTF_8)) {
            JsonObject status = new JsonObject();
            status.add("lastRefresh", new JsonPrimitive(System.currentTimeMillis()));
            GsonProvider.prettyPrinting().toJson(status, writer);
        } catch (IOException e) {
            // ignore
        }
    }

    private long readLastRefreshTime() {
        Path statusFile = this.plugin.getTranslationManager().getRepositoryStatusFile();

        if (Files.exists(statusFile)) {
            try (BufferedReader reader = Files.newBufferedReader(statusFile, StandardCharsets.UTF_8)) {
                JsonObject status = GsonProvider.normal().fromJson(reader, JsonObject.class);
                if (status.has("lastRefresh")) {
                    return status.get("lastRefresh").getAsLong();
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return 0L;
    }

    private MetadataResponse getTranslationsMetadata() throws IOException, UnsuccessfulRequestException {
        Request request = new Request.Builder().header("User-Agent", "floracore").url(TRANSLATIONS_INFO_ENDPOINT).build();

        JsonObject jsonResponse;
        try (Response response = abstractHttpClient.makeHttpRequest(request)) {
            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) {
                    throw new RuntimeException("No response");
                }

                try (InputStream inputStream = new LimitedInputStream(responseBody.byteStream(), MAX_BUNDLE_SIZE)) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        jsonResponse = GsonProvider.normal().fromJson(reader, JsonObject.class);
                    }
                }
            }
        }

        List<LanguageInfo> languages = new ArrayList<>();
        for (Map.Entry<String, JsonElement> language : jsonResponse.get("languages").getAsJsonObject().entrySet()) {
            languages.add(new LanguageInfo(language.getKey(), language.getValue().getAsJsonObject()));
        }
        languages.removeIf(language -> language.progress() <= 0);

        if (languages.size() >= 100) {
            // just a precaution: if more than 100 languages have been
            // returned then the metadata server is doing something silly
            throw new IOException("More than 100 languages - cancelling download");
        }

        long cacheMaxAge = jsonResponse.get("cacheMaxAge").getAsLong();

        return new MetadataResponse(cacheMaxAge, languages);
    }

    private static final class MetadataResponse {
        private final long cacheMaxAge;
        private final List<LanguageInfo> languages;

        MetadataResponse(long cacheMaxAge, List<LanguageInfo> languages) {
            this.cacheMaxAge = cacheMaxAge;
            this.languages = languages;
        }
    }

    public static final class LanguageInfo {
        private final String id;
        private final String name;
        private final Locale locale;
        private final int progress;
        private final List<String> contributors;

        LanguageInfo(String id, JsonObject data) {
            this.id = id;
            this.name = data.get("name").getAsString();
            this.locale = Objects.requireNonNull(TranslationManager.parseLocale(data.get("localeTag").getAsString()));
            this.progress = data.get("progress").getAsInt();
            this.contributors = new ArrayList<>();
            for (JsonElement contributor : data.get("contributors").getAsJsonArray()) {
                this.contributors.add(contributor.getAsJsonObject().get("name").getAsString());
            }
        }

        public String id() {
            return this.id;
        }

        public String name() {
            return this.name;
        }

        public Locale locale() {
            return this.locale;
        }

        public int progress() {
            return this.progress;
        }

        public List<String> contributors() {
            return this.contributors;
        }
    }

    private static final class LimitedInputStream extends FilterInputStream implements Closeable {
        private final long limit;
        private long count;

        public LimitedInputStream(InputStream inputStream, long limit) {
            super(inputStream);
            this.limit = limit;
        }

        private void checkLimit() throws IOException {
            if (this.count > this.limit) {
                throw new IOException("Limit exceeded");
            }
        }

        @Override
        public int read() throws IOException {
            int res = super.read();
            if (res != -1) {
                this.count++;
                checkLimit();
            }
            return res;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int res = super.read(b, off, len);
            if (res > 0) {
                this.count += res;
                checkLimit();
            }
            return res;
        }
    }
}
