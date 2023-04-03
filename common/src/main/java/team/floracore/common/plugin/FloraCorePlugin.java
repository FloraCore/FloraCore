package team.floracore.common.plugin;

import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.plugin.logging.*;

public interface FloraCorePlugin {
    /**
     * Gets the bootstrap plugin instance
     *
     * @return the bootstrap plugin
     */
    FloraCoreBootstrap getBootstrap();

    /**
     * Gets a wrapped logger instance for the platform.
     *
     * @return the plugin's logger
     */
    PluginLogger getLogger();
}
