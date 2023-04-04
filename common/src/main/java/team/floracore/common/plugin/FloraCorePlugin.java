package team.floracore.common.plugin;

import team.floracore.common.config.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.plugin.logging.*;
import team.floracore.common.storage.*;

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

    /**
     * Gets the dependency manager for the plugin
     *
     * @return the dependency manager
     */
    DependencyManager getDependencyManager();

    /**
     * Gets the plugin's configuration
     *
     * @return the plugin config
     */
    FloraCoreConfiguration getConfiguration();

    /**
     * Gets the primary data storage instance. This is likely to be wrapped with extra layers for caching, etc.
     *
     * @return the storage handler instance
     */
    Storage getStorage();
}
