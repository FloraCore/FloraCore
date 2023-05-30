package team.floracore.common.listener;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;

import java.util.concurrent.*;

public abstract class AbstractFloraCoreListener implements FloraCoreListener {
    private final FloraCorePlugin plugin;

    public AbstractFloraCoreListener(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public StorageImplementation getStorageImplementation() {
        return plugin.getStorage().getImplementation();
    }

    @Override
    public Executor getAsyncExecutor() {
        return plugin.getBootstrap().getScheduler().async();
    }
}
