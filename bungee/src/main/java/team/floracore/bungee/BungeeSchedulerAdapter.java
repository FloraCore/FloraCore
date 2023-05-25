package team.floracore.bungee;

import net.md_5.bungee.api.scheduler.*;
import team.floracore.common.plugin.scheduler.*;
import team.floracore.common.util.*;

import java.util.*;
import java.util.concurrent.*;

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
