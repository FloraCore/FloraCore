package team.floracore.common.storage.misc.floracore;

import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;

public abstract class AbstractFloraCoreTable implements FloraCoreTable {
    private final FloraCorePlugin plugin;
    private final StorageImplementation storageImplementation;

    public AbstractFloraCoreTable(FloraCorePlugin plugin, StorageImplementation storageImplementation) {
        this.plugin = plugin;
        this.storageImplementation = storageImplementation;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public StorageImplementation getStorageImplementation() {
        return storageImplementation;
    }
}
