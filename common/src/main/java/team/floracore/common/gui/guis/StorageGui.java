package team.floracore.common.gui.guis;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.*;

import java.util.*;

/**
 * GUI that does not clear its items once it's closed
 */
public class StorageGui extends BaseGui {

    /**
     * Main constructor for the StorageGui
     *
     * @param rows                 The amount of rows the GUI should have
     * @param title                The GUI's title using {@link String}
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     * @since 3.0.3
     */
    public StorageGui(final int rows, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }

    /**
     * Main constructor of the Persistent GUI
     *
     * @param rows  The rows the GUI should have
     * @param title The GUI's title
     */
    @Deprecated
    public StorageGui(final int rows, @NotNull final String title) {
        super(rows, title);
    }

    /**
     * Alternative constructor that does not require rows
     *
     * @param title The GUI's title
     */
    @Deprecated
    public StorageGui(@NotNull final String title) {
        super(1, title);
    }

    /**
     * Adds {@link ItemStack} to the inventory straight, not the GUI
     *
     * @param items Varargs with {@link ItemStack}s
     * @return An immutable {@link Map} with the left overs
     */
    @NotNull
    public Map<@NotNull Integer, @NotNull ItemStack> addItem(@NotNull final ItemStack... items) {
        return Collections.unmodifiableMap(getInventory().addItem(items));
    }

    /**
     * Adds {@link ItemStack} to the inventory straight, not the GUI
     *
     * @param items Varargs with {@link ItemStack}s
     * @return An immutable {@link Map} with the left overs
     */
    public Map<@NotNull Integer, @NotNull ItemStack> addItem(@NotNull final List<ItemStack> items) {
        return addItem(items.toArray(new ItemStack[0]));
    }

    /**
     * Overridden {@link BaseGui#open(HumanEntity)} to prevent
     *
     * @param player The {@link HumanEntity} to open the GUI to
     */
    @Override
    public void open(@NotNull final HumanEntity player) {
        if (player.isSleeping()) return;
        populateGui();
        player.openInventory(getInventory());
    }

}
