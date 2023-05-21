package team.floracore.paper;

import team.floracore.common.plugin.scheduler.*;

import java.util.concurrent.*;

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
