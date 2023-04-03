package team.floracore.common.plugin;

import team.floracore.common.dependencies.*;
import team.floracore.common.plugin.logging.*;

import java.time.*;

public abstract class AbstractFloraCorePlugin implements FloraCorePlugin {
    // init during load
    private DependencyManager dependencyManager;

    /**
     * Performs the initial actions to load the plugin
     */
    public final void onLoad() {
        // load dependencies
        this.dependencyManager = createDependencyManager();
    }

    public final void onEnable() {
        Duration timeTaken = Duration.between(getBootstrap().getStartupTime(), Instant.now());
        getLogger().info("Successfully enabled. (took " + timeTaken.toMillis() + "ms)");
    }

    public final void onDisable() {
        getLogger().info("Starting shutdown process...");

        // cancel delayed/repeating tasks
        getBootstrap().getScheduler().shutdownScheduler();

        // shutdown async executor pool
        getBootstrap().getScheduler().shutdownExecutor();

        // close isolated loaders for non-relocated dependencies
        getDependencyManager().close();
        // close classpath appender
        getBootstrap().getClassPathAppender().close();

        getLogger().info("Goodbye!");
    }

    protected DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }

    @Override
    public DependencyManager getDependencyManager() {
        return this.dependencyManager;
    }

    @Override
    public PluginLogger getLogger() {
        return getBootstrap().getPluginLogger();
    }
}
