package team.floracore.bukkit;

import team.floracore.common.plugin.scheduler.AbstractJavaScheduler;
import team.floracore.common.plugin.scheduler.SchedulerAdapter;

import java.util.concurrent.Executor;

public class BukkitSchedulerAdapter extends AbstractJavaScheduler implements SchedulerAdapter {
	private final Executor sync;

	public BukkitSchedulerAdapter(FCBukkitBootstrap bootstrap) {
		super(bootstrap);
		this.sync = r -> bootstrap.getServer().getScheduler().scheduleSyncDelayedTask(bootstrap.getLoader(), r);
	}

	@Override
	public Executor sync() {
		return this.sync;
	}

}
