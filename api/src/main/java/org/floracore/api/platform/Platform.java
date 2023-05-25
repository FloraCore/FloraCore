package org.floracore.api.platform;

import org.checkerframework.checker.nullness.qual.*;

import java.time.*;

/**
 * Provides information about the platform LuckPerms is running on.
 */
public interface Platform {

    /**
     * Gets the type of platform LuckPerms is running on
     *
     * @return the type of platform LuckPerms is running on
     */
    Platform.@NonNull Type getType();

    /**
     * Gets the time when the plugin first started.
     *
     * @return the enable time
     */
    @NonNull Instant getStartTime();

    /**
     * Represents a type of platform which LuckPerms can run on.
     */
    enum Type {
        BUKKIT("Bukkit"),
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
        public @NonNull String getFriendlyName() {
            return this.friendlyName;
        }
    }
}
