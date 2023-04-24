package team.floracore.common.locale.data;

import com.google.gson.*;
import okhttp3.*;
import team.floracore.common.config.*;
import team.floracore.common.http.*;
import team.floracore.common.plugin.*;
import team.floracore.common.util.gson.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;

public class NamesRepository {
    private static final String NAMES_DOWNLOAD_ENDPOINT = "https://fc-meta.kinomc.net/data/names";
    private static final long CACHE_MAX_AGE = TimeUnit.DAYS.toMillis(7);
    private final FloraCorePlugin plugin;
    private final AbstractHttpClient abstractHttpClient;

    public NamesRepository(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.abstractHttpClient = new AbstractHttpClient(plugin.getHttpClient());
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

        // perform a refresh!
        downloadNames(true);
    }

    public void downloadNames(boolean updateStatus) {
        DataManager manager = this.plugin.getDataManager();
        Path dataDirectory = manager.getDataDirectory();
        Request request = new Request.Builder().header("User-Agent", "floracore").url(NAMES_DOWNLOAD_ENDPOINT).build();
        Path file = dataDirectory.resolve("names.csv");
        try (Response response = abstractHttpClient.makeHttpRequest(request)) {
            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) {
                    throw new IOException("No response");
                }
                try (InputStream inputStream = responseBody.byteStream()) {
                    Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (UnsuccessfulRequestException | IOException ignored) {
        }
        if (updateStatus) {
            writeLastRefreshTime();
        }
        manager.reload();
    }

    private void writeLastRefreshTime() {
        Path statusFile = getRepositoryStatusFile();
        try (BufferedWriter writer = Files.newBufferedWriter(statusFile, StandardCharsets.UTF_8)) {
            JsonObject status = new JsonObject();
            status.add("lastRefresh", new JsonPrimitive(System.currentTimeMillis()));
            GsonProvider.prettyPrinting().toJson(status, writer);
        } catch (IOException e) {
            // ignore
        }
    }

    private long readLastRefreshTime() {
        Path statusFile = getRepositoryStatusFile();

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

    public Path getRepositoryStatusFile() {
        return this.plugin.getDataManager().getDataDirectory().resolve("status.json");
    }
}
