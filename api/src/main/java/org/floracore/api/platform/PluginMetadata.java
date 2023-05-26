package org.floracore.api.platform;

import org.jetbrains.annotations.*;

/**
 * Provides information about the FloraCore plugin.
 */
public interface PluginMetadata {

    /**
     * Gets the plugin version
     *
     * @return the version of the plugin running on the platform
     */
    @NotNull
    String getVersion();

    /**
     * Gets the API version
     *
     * @return the version of the API running on the platform
     */
    @NotNull
    String getApiVersion();

}
