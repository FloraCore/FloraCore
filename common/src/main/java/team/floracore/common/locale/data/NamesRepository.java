package team.floracore.common.locale.data;

import com.google.gson.*;
import com.opencsv.*;
import com.opencsv.exceptions.*;
import okhttp3.*;
import team.floracore.common.config.*;
import team.floracore.common.http.*;
import team.floracore.common.plugin.*;
import team.floracore.common.util.gson.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class NamesRepository {
    private static final String NAMES_DOWNLOAD_ENDPOINT = "https://fc-meta.kinomc.net/data/names";
    private static final long CACHE_MAX_AGE = TimeUnit.HOURS.toMillis(6);
    private final FloraCorePlugin plugin;
    private final AbstractHttpClient abstractHttpClient;
    private List<NameProperty> namePropertyList;

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
                namePropertyList = loadNamesCSVData();
            } catch (Exception e) {
                // ignore
            }
        });
    }

    public List<NameProperty> loadNamesCSVData() throws IOException, CsvValidationException {
        Path filePath = getNamesCSVFile();
        int expectedSize = 10000; // 设置预期数据行数
        List<NameProperty> ret = new ArrayList<>(expectedSize);
        try (CSVReader reader = new CSVReaderBuilder(Files.newBufferedReader(filePath, StandardCharsets.UTF_8))
                // 跳过第一行
                .withSkipLines(1).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String name = nextLine[0];
                String value = nextLine[1];
                String signature = nextLine[2];
                ret.add(new NameProperty(name, value, signature));
            }
        }
        return ret;
    }

    private void refresh() {
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

    public NameProperty getRandomNameProperty() {
        if (namePropertyList == null || namePropertyList.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(namePropertyList.size());
        return namePropertyList.get(index);
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

    public Path getNamesCSVFile() {
        return this.plugin.getDataManager().getDataDirectory().resolve("names.csv");
    }

    public static class NameProperty {
        private final String name;
        private final String value;
        private final String signature;

        public NameProperty(String name, String value, String signature) {
            this.name = name;
            this.value = value;
            this.signature = signature;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getSignature() {
            return signature;
        }
    }
}
