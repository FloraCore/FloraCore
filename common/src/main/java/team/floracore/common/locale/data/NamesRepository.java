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
    private final HashMap<String, NameProperty> nameProperty = new HashMap<>();

    public NamesRepository(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.abstractHttpClient = new AbstractHttpClient(plugin.getHttpClient());
    }

    public void scheduleRefreshRepeating() {
        this.plugin.getBootstrap().getScheduler().asyncRepeating(this::scheduleRefresh, 6, TimeUnit.HOURS);
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
                loadNamesCSVData();
            } catch (Exception e) {
                // ignore
            }
        });
    }

    public void loadNamesCSVData() throws IOException, CsvValidationException {
        Path filePath = getNamesCSVFile();
        int expectedSize = 10000; // 设置预期数据行数
        try (CSVReader reader = new CSVReaderBuilder(Files.newBufferedReader(filePath, StandardCharsets.UTF_8))
                // 跳过第一行
                .withSkipLines(1).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String name = nextLine[0];
                String value = nextLine[1];
                String signature = nextLine[2];
                nameProperty.put(name, new NameProperty(name, value, signature));
            }
        }
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
        if (nameProperty.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(nameProperty.size());

        return (new ArrayList<>(nameProperty.values())).get(index);
    }

    public NameProperty getNameProperty(String name) {
        return nameProperty.get(name);
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

    public NameProperty getSteveProperty() {
        String value = "ewogICJ0aW1lc3RhbXAiIDogMTY4MjMzMDg1NjU1NiwKICAicHJvZmlsZUlkIiA6ICJkZTFlMTRiZjFmMmQ0MGY1OWZlMzI4ZTU5ZjkzMDljMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJGcmVlZG9tRGFQdWciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY1ZDJjOGE0ZjI4M2JkMWNkZjhkMjE2MjcyNDVjMjFiMTEwNTU3MjM5ZWUxMWI2ZGM0ZjY3Y2EwYTViOWIzYyIKICAgIH0KICB9Cn0=";
        String signature = "daTubyLTq2QvhOXfLzqSGoELandwx3I2JHlXrmYdxlyHdv46ywYPsUhY1VNLIB0krRAA/8R82q2iNbcRIWBR4OmNVGKUALVc2jBurZstFYuanoW9fEYTR0BqxWMR6XzfzE5X83uT56EmTEXH4SpRuxTw21Pm7IXQcFcuG+/rb91djrOWQ+5xQsj4T/CGMlaw/WjwpF9PtqtjVkdmcIJ4OM6pYTWdvTShEjKEByAW2Sl1pklshSqu4kl+zHbHaXyF3ec/fA84IKmJQx6y5ypzA2E3PfptwTsUCbA1I9lvNVE1KNdUuZBGty+AZ7RHohveKUyh7/2wL4CuboWMNc9vAWHIeWz0uX9sqZRQSwRW78qpvdubcBM9IzEw6hfbekn4S8jidKe42Jb6dCsVop50Uqm36vrr5nQf321Hl1MtBaKxWjFGD1Uhj6I2rMMi40zOGzkhLFhoM0povKtL4Wi/bxabMGRbHBSWD+LHfJjf1wDV5GlT11QyPdVTi6vSIv3x3jN/LOSqCBuqKxz1ojLMKT1LVthY8HPjXqqJnDI0UmI0c9qDGDe5/oBvX7IHyCz1OBdy6iJ54ucNrs02eKBTgwhFVTXlnuLyBKmca/ZpuyNVZ3sRO3drET80iM2GlrgTJYA7DzfQnPgyyAHHDqyqZT+ksLVATjRzLtaPeJ9tM+Y=";
        return new NameProperty("Steve", value, signature);
    }

    public NameProperty getAlexProperty() {
        String value = "ewogICJ0aW1lc3RhbXAiIDogMTY4MjMzMTA4NTQwNSwKICAicHJvZmlsZUlkIiA6ICI2OGUxMmE5MDlkNjc0ZWEyOGNmOTc1NDczNzg5ZjNjOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJKaWNrTG92ZXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFjOGE0ZGE2NWZkMzFlOGY4Y2VkODcxZjk5ZTlhOTEyZWYzMTFhYWQ2NjcwYWQ0OTNhYjBjZDE3ZDA3OTY1NSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
        String signature = "Rzvbti/OFMHzbFe3jRD0SfFfR1jyZFhwWB7zct+JQNO0Z4+AdaES9lE2oPV0HI2Y8KPJHgvnsmYI2bHaGhi4nyZO5ExrBHZbooG52RpEpAatCY506HbG15B52ZDy01BTwnZIkN/bN96IfeCoz/28W/il2fxoEt//fRp8tcW3nmLp6r0zBBIm2U789/EZqynPvGneZPcI52CdrhSi8tYL7F7nt2Gb2LdR0e+TJchVJy5gXQBxKTwNcJ9OOWvaANfysbyXiGHitdKNZL4UPZ41xGsTluqI3gFrbAR6kdaPOOffP1cx1+hnzDddKVDfRnOvEsKJuz4SBDYcJVLshyWA2OvA7iKmBVkwR+eg7tbxTZciSyLvTkAziKvvnhQKXCWjJlMHtJLsJ/Ici2iZ0NjDlv4w4vCc2SqfOipY+oUJneqYtRwAop1Zl4/JrZsC5KHT709DF7gBfos9gro+l9O7cgdT0BIwnqiYcT8sImrXj1mRU0y5qjXm/UbugjxDHWfB6VkevA+uu2GiluE7iBcj7FUGiQl1g3CYGqMAfYCqkqmkYqIEPyqWAoYMEg2Tkm0hJXxUqWWfH+7zHkRXKdGz2+gwCde9gNZj1rrA5yxLdjzMB7VXjpNC92g/brV51cAQFakUkagWTVtHOP/w85dr6zRb5FV7uiR7z1L647cJlpQ=";
        return new NameProperty("Alex", value, signature);
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
