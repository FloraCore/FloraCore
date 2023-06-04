package team.floracore.bungee;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import team.floracore.common.plugin.scheduler.SchedulerAdapter;
import team.floracore.common.plugin.scheduler.SchedulerTask;
import team.floracore.common.util.Iterators;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class BungeeSchedulerAdapter implements SchedulerAdapter {
	private final FCBungeeBootstrap bootstrap;

	private final Executor executor;
	private final Set<ScheduledTask> tasks = Collections.newSetFromMap(new WeakHashMap<>());

	public BungeeSchedulerAdapter(FCBungeeBootstrap bootstrap) {
		this.bootstrap = bootstrap;
		this.executor = r -> bootstrap.getProxy().getScheduler().runAsync(bootstrap.getLoader(), r);
	}

	@Override
	public Executor async() {
		return this.executor;
	}

	@Override
	public Executor sync() {
		return this.executor;
	}

	@Override
	public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
		ScheduledTask t = this.bootstrap.getProxy()
		                                .getScheduler()
		                                .schedule(this.bootstrap.getLoader(), task, delay, unit);
		this.tasks.add(t);
		return t::cancel;
	}

	@Override
	public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
		ScheduledTask t = this.bootstrap.getProxy()
		                                .getScheduler()
		                                .schedule(this.bootstrap.getLoader(), task, interval, interval, unit);
		this.tasks.add(t);
		return t::cancel;
	}

	@Override
	public void shutdownScheduler() {
		Iterators.tryIterate(this.tasks, ScheduledTask::cancel);
	}

	@Override
	public void shutdownExecutor() {
		// do nothing
	}
}
