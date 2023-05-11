package team.floracore.common.gui.components.util;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

public final class SkullUtil {

    /**
     * The main SKULL material for the version
     */
    private static final Material SKULL = getSkullMaterial();

    /**
     * Gets the correct {@link Material} for the version
     *
     * @return The correct SKULL {@link Material}
     */
    private static Material getSkullMaterial() {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return Material.valueOf("SKULL_ITEM");
        }

        return Material.PLAYER_HEAD;
    }

    /**
     * Create a player skull
     *
     * @return player skull
     */
    @SuppressWarnings("deprecation")
    public static ItemStack skull() {
        return VersionHelper.IS_ITEM_LEGACY ? new ItemStack(SKULL, 1, (short) 3) : new ItemStack(SKULL);
    }

    /**
     * Checks if an {@link ItemStack} is a player skull
     *
     * @param item item to check
     * @return whether the item is a player skull
     */
    @SuppressWarnings("deprecation")
    public static boolean isPlayerSkull(@NotNull final ItemStack item) {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return item.getType() == SKULL && item.getDurability() == (short) 3;
        }

        return item.getType() == SKULL;
    }

}
