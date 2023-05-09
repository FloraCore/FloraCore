package team.floracore.common.gui.components.util;

import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.*;
import team.floracore.common.gui.components.exception.*;
import team.floracore.common.gui.guis.*;

import java.util.*;

/**
 * TODO fix comments
 */
public final class GuiFiller {

    private final BaseGui gui;

    public GuiFiller(final BaseGui gui) {
        this.gui = gui;
    }

    /**
     * Fills top portion of the GUI
     *
     * @param guiItem GuiItem
     */
    public void fillTop(@NotNull final GuiItem guiItem) {
        fillTop(Collections.singletonList(guiItem));
    }

    /**
     * Fills top portion of the GUI with alternation
     *
     * @param guiItems List of GuiItems
     */
    public void fillTop(@NotNull final List<GuiItem> guiItems) {
        final List<GuiItem> items = repeatList(guiItems);
        for (int i = 0; i < 9; i++) {
            if (!gui.getGuiItems().containsKey(i)) gui.setItem(i, items.get(i));
        }
    }

    /**
     * Fills bottom portion of the GUI
     *
     * @param guiItem GuiItem
     */
    public void fillBottom(@NotNull final GuiItem guiItem) {
        fillBottom(Collections.singletonList(guiItem));
    }

    /**
     * Fills bottom portion of the GUI with alternation
     *
     * @param guiItems GuiItem
     */
    public void fillBottom(@NotNull final List<GuiItem> guiItems) {
        final int rows = gui.getRows();
        final List<GuiItem> items = repeatList(guiItems);
        for (int i = 9; i > 0; i--) {
            if (gui.getGuiItems().get((rows * 9) - i) == null) {
                gui.setItem((rows * 9) - i, items.get(i));
            }
        }
    }

    /**
     * Fills the outside section of the GUI with a GuiItem
     *
     * @param guiItem GuiItem
     */
    public void fillBorder(@NotNull final GuiItem guiItem) {
        fillBorder(Collections.singletonList(guiItem));
    }

    /**
     * Fill empty slots with Multiple GuiItems, goes through list and starts again
     *
     * @param guiItems GuiItem
     */
    public void fillBorder(@NotNull final List<GuiItem> guiItems) {
        final int rows = gui.getRows();
        if (rows <= 2) return;

        final List<GuiItem> items = repeatList(guiItems);

        for (int i = 0; i < rows * 9; i++) {
            if ((i <= 8)
                    || (i >= (rows * 9) - 8) && (i <= (rows * 9) - 2)
                    || i % 9 == 0
                    || i % 9 == 8)
                gui.setItem(i, items.get(i));

        }
    }

    /**
     * Fills rectangle from points within the GUI
     *
     * @param rowFrom Row point 1
     * @param colFrom Col point 1
     * @param rowTo   Row point 2
     * @param colTo   Col point 2
     * @param guiItem Item to fill with
     * @author Harolds
     */
    public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, @NotNull final GuiItem guiItem) {
        fillBetweenPoints(rowFrom, colFrom, rowTo, colTo, Collections.singletonList(guiItem));
    }

    /**
     * Fills rectangle from points within the GUI
     *
     * @param rowFrom  Row point 1
     * @param colFrom  Col point 1
     * @param rowTo    Row point 2
     * @param colTo    Col point 2
     * @param guiItems Item to fill with
     * @author Harolds
     */
    public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, @NotNull final List<GuiItem> guiItems) {
        final int minRow = Math.min(rowFrom, rowTo);
        final int maxRow = Math.max(rowFrom, rowTo);
        final int minCol = Math.min(colFrom, colTo);
        final int maxCol = Math.max(colFrom, colTo);

        final int rows = gui.getRows();
        final List<GuiItem> items = repeatList(guiItems);

        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= 9; col++) {
                final int slot = getSlotFromRowCol(row, col);
                if (!((row >= minRow && row <= maxRow) && (col >= minCol && col <= maxCol)))
                    continue;

                gui.setItem(slot, items.get(slot));
            }
        }
    }

    /**
     * Sets an GuiItem to fill up the entire inventory where there is no other item
     *
     * @param guiItem The item to use as fill
     */
    public void fill(@NotNull final GuiItem guiItem) {
        fill(Collections.singletonList(guiItem));
    }

    /**
     * Fill empty slots with Multiple GuiItems, goes through list and starts again
     *
     * @param guiItems GuiItem
     */
    public void fill(@NotNull final List<GuiItem> guiItems) {
        if (gui instanceof PaginatedGui) {
            throw new GuiException("Full filling a GUI is not supported in a Paginated GUI!");
        }

        final GuiType type = gui.guiType();

        final int fill;
        if (type == GuiType.CHEST) {
            fill = gui.getRows() * type.getLimit();
        } else {
            fill = type.getLimit();
        }

        final List<GuiItem> items = repeatList(guiItems);
        for (int i = 0; i < fill; i++) {
            if (gui.getGuiItems().get(i) == null) gui.setItem(i, items.get(i));
        }
    }

    /**
     * Repeats a list of items. Allows for alternating items
     * Stores references to existing objects -> Does not create new objects
     *
     * @param guiItems List of items to repeat
     * @return New list
     */
    private List<GuiItem> repeatList(@NotNull final List<GuiItem> guiItems) {
        final List<GuiItem> repeated = new ArrayList<>();
        Collections.nCopies(gui.getRows() * 9, guiItems).forEach(repeated::addAll);
        return repeated;
    }

    /**
     * Gets the slot from the row and col passed
     *
     * @param row The row
     * @param col The col
     * @return The new slot
     */
    private int getSlotFromRowCol(final int row, final int col) {
        return (col + (row - 1) * 9) - 1;
    }

}
