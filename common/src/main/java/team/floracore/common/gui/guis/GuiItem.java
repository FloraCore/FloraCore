package team.floracore.common.gui.guis;

import org.apache.commons.lang3.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.*;
import team.floracore.common.gui.components.util.*;

import java.util.*;

/**
 * GuiItem represents the {@link ItemStack} on the {@link Inventory}
 */
@SuppressWarnings("unused")
public class GuiItem {

    // Random UUID to identify the item when clicking
    private final UUID uuid = UUID.randomUUID();
    // Action to do when clicking on the item
    private GuiAction<InventoryClickEvent> action;
    // The ItemStack of the GuiItem
    private ItemStack itemStack;

    /**
     * Main constructor of the GuiItem
     *
     * @param itemStack The {@link ItemStack} to be used
     * @param action    The {@link GuiAction} to run when clicking on the Item
     */
    public GuiItem(@NotNull final ItemStack itemStack, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        Validate.notNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.action = action;

        // Sets the UUID to an NBT tag to be identifiable later
        this.itemStack = ItemNbt.setString(itemStack, "mf-gui", uuid.toString());
    }

    /**
     * Secondary constructor with no action
     *
     * @param itemStack The ItemStack to be used
     */
    public GuiItem(@NotNull final ItemStack itemStack) {
        this(itemStack, null);
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack} but without a {@link GuiAction}
     *
     * @param material The {@link Material} to be used when invoking class
     */
    public GuiItem(@NotNull final Material material) {
        this(new ItemStack(material), null);
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack}
     *
     * @param material The {@code Material} to be used when invoking class
     * @param action   The {@link GuiAction} should be passed on {@link InventoryClickEvent}
     */
    public GuiItem(@NotNull final Material material, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }

    /**
     * Gets the GuiItem's {@link ItemStack}
     *
     * @return The {@link ItemStack}
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Replaces the {@link ItemStack} of the GUI Item
     *
     * @param itemStack The new {@link ItemStack}
     */
    public void setItemStack(@NotNull final ItemStack itemStack) {
        Validate.notNull(itemStack, "The ItemStack for the GUI Item cannot be null!");
        this.itemStack = ItemNbt.setString(itemStack, "mf-gui", uuid.toString());
    }

    /**
     * Gets the random {@link UUID} that was generated when the GuiItem was made
     */
    @NotNull
    UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the {@link GuiAction} to do when the player clicks on it
     */
    @Nullable
    GuiAction<InventoryClickEvent> getAction() {
        return action;
    }

    /**
     * Replaces the {@link GuiAction} of the current GUI Item
     *
     * @param action The new {@link GuiAction} to set
     */
    public void setAction(@Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        this.action = action;
    }

}
