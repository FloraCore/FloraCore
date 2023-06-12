package team.floracore.common.locale.data;

import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.MoreFiles;

import java.io.IOException;
import java.nio.file.Path;

public class DataManager {
    private final FloraCorePlugin plugin;
    private final Path dataDirectory;

    public DataManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.dataDirectory = this.plugin.getBootstrap().getConfigDirectory().resolve("data");

        try {
            MoreFiles.createDirectoriesIfNotExists(this.dataDirectory);
        } catch (IOException e) {
            // ignore
        }
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public void reload() {

    }
}
