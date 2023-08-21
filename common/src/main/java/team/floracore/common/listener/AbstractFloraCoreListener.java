package team.floracore.common.listener;

import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;

import java.util.concurrent.Executor;

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
