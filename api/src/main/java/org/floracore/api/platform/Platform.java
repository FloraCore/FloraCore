package org.floracore.api.platform;

import org.jetbrains.annotations.*;

import java.time.*;

/**
 * Provides information about the platform FloraCore is running on.
 */
public interface Platform {

    /**
     * Gets the type of platform FloraCore is running on
     *
     * @return the type of platform FloraCore is running on
     */
    Platform.@NotNull Type getType();

    /**
     * Gets the time when the plugin first started.
     *
     * @return the enabled time
     */
    @NotNull
    Instant getStartTime();

    /**
     * Represents a type of platform which FloraCore can run on.
     */
    enum Type {
        /**
         * 插件是在Bukkit核心中启动的。
         */
        BUKKIT("Bukkit"),
        /**
         * 插件是在BungeeCord核心中启动的。
         */
        BUNGEECORD("BungeeCord");

        private final String friendlyName;

        Type(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        /**
         * Gets a readable name for the platform type.
         *
         * @return a readable name
         */
        public @NotNull String getFriendlyName() {
            return this.friendlyName;
        }
    }
}
