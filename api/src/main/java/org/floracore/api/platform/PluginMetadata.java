package org.floracore.api.platform;

import org.checkerframework.checker.nullness.qual.*;

/**
 * Provides information about the FloraCore plugin.
 */
public interface PluginMetadata {

    /**
     * Gets the plugin version
     *
     * @return the version of the plugin running on the platform
     */
    @NonNull String getVersion();

    /**
     * Gets the API version
     *
     * @return the version of the API running on the platform
     */
    @NonNull String getApiVersion();

}
