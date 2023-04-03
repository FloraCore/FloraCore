package team.floracore.common.plugin;

import team.floracore.common.plugin.logging.*;

import java.time.*;

public abstract class AbstractFloraCorePlugin implements FloraCorePlugin {
    /**
     * Performs the initial actions to load the plugin
     */
    public final void onLoad() {

    }

    public final void onEnable() {
        Duration timeTaken = Duration.between(getBootstrap().getStartupTime(), Instant.now());
        getLogger().info("Successfully enabled. (took " + timeTaken.toMillis() + "ms)");
    }

    public final void onDisable() {
        getLogger().info("Starting shutdown process...");

        // close classpath appender
        getBootstrap().getClassPathAppender().close();

        getLogger().info("Goodbye!");
    }

    @Override
    public PluginLogger getLogger() {
        return getBootstrap().getPluginLogger();
    }
}
