package org.floracore.api.platform;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents an entity on the server.
 *
 * <p>This does not relate directly to a "Minecraft Entity". The closest
 * comparison is to a "CommandSender" or "CommandSource".</p>
 *
 * <p>The various types of {@link PlatformEntity} are detailed in {@link Type}.</p>
 */
public interface PlatformEntity {

    /**
     * Gets the unique id of the entity, if it has one.
     *
     * <p>For players, this returns their uuid assigned by the server.</p>
     *
     * @return the uuid of the object, if available
     */
    @Nullable UUID getUniqueId();

    /**
     * Gets the name of the object
     *
     * @return the object name
     */
    @NotNull
    String getName();

    /**
     * Gets the entities type.
     *
     * @return the type
     */
    @NotNull
    Type getType();

    /**
     * The different types of {@link PlatformEntity}
     */
    enum Type {

        /**
         * Represents a player connected to the server
         */
        PLAYER,

        /**
         * Represents the server console
         */
        CONSOLE
    }

}
