package team.floracore.common.locale.data;

import team.floracore.common.plugin.*;
import team.floracore.common.util.*;

import java.io.*;
import java.nio.file.*;

public class DataManager {
    private final FloraCorePlugin plugin;
    private final Path dataDirectory;
    private final NamesRepository namesRepository;

    public DataManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.dataDirectory = this.plugin.getBootstrap().getConfigDirectory().resolve("data");

        try {
            MoreFiles.createDirectoriesIfNotExists(this.dataDirectory);
        } catch (IOException e) {
            // ignore
        }

        this.namesRepository = new NamesRepository(plugin);
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public NamesRepository getNamesRepository() {
        return namesRepository;
    }

    public void reload() {

    }
}
