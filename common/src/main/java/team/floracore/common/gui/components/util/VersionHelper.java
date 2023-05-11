package team.floracore.common.gui.components.util;

import com.google.common.primitives.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.exception.*;

import java.util.regex.*;

/**
 * Class for detecting server version for legacy support :(
 */
public final class VersionHelper {

    private static final String NMS_VERSION = getNmsVersion();

    // Unbreakable change
    private static final int V1_11 = 1110;
    // Material and components on items change
    private static final int V1_13 = 1130;
    // PDC and customModelData
    private static final int V1_14 = 1140;
    // Paper adventure changes
    private static final int V1_16_5 = 1165;
    // SkullMeta#setOwningPlayer was added
    private static final int V1_12_1 = 1121;

    private static final int CURRENT_VERSION = getCurrentVersion();
    /**
     * Checks if the version supports Components or not
     * Spigot always false
     */
    public static final boolean IS_COMPONENT_LEGACY = CURRENT_VERSION < V1_16_5;
    /**
     * Checks if the version is lower than 1.13 due to the item changes
     */
    public static final boolean IS_ITEM_LEGACY = CURRENT_VERSION < V1_13;
    /**
     * Checks if the version supports the {@link org.bukkit.inventory.meta.ItemMeta#setUnbreakable(boolean)} method
     */
    public static final boolean IS_UNBREAKABLE_LEGACY = CURRENT_VERSION < V1_11;
    /**
     * Checks if the version supports {@link org.bukkit.persistence.PersistentDataContainer}
     */
    public static final boolean IS_PDC_VERSION = CURRENT_VERSION >= V1_14;
    /**
     * Checks if the version doesn't have {@link org.bukkit.inventory.meta.SkullMeta#setOwningPlayer(OfflinePlayer)} and
     * {@link org.bukkit.inventory.meta.SkullMeta#setOwner(String)} should be used instead
     */
    public static final boolean IS_SKULL_OWNER_LEGACY = CURRENT_VERSION < V1_12_1;
    /**
     * Checks if the version has {@link org.bukkit.inventory.meta.ItemMeta#setCustomModelData(Integer)}
     */
    public static final boolean IS_CUSTOM_MODEL_DATA = CURRENT_VERSION >= V1_14;
    private static final boolean IS_PAPER = checkPaper();

    /**
     * Check if the server has access to the Paper API
     * Taken from <a href="https://github.com/PaperMC/PaperLib">PaperLib</a>
     *
     * @return True if on Paper server (or forks), false anything else
     */
    private static boolean checkPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    /**
     * Gets the current server version
     *
     * @return A protocol like number representing the version, for example 1.16.5 - 1165
     */
    private static int getCurrentVersion() {
        // No need to cache since will only run once
        final Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());

        final StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            final String patch = matcher.group("patch");
            if (patch == null) stringBuilder.append("0");
            else stringBuilder.append(patch.replace(".", ""));
        }

        //noinspection UnstableApiUsage
        final Integer version = Ints.tryParse(stringBuilder.toString());

        // Should never fail
        if (version == null) throw new GuiException("Could not retrieve server version!");

        return version;
    }

    private static String getNmsVersion() {
        final String version = Bukkit.getServer().getClass().getPackage().getName();
        return version.substring(version.lastIndexOf('.') + 1);
    }

    public static Class<?> craftClass(@NotNull final String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + "." + name);
    }

}
