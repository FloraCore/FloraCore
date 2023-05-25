package team.floracore.bukkit.inevntory.opener;

import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import team.floracore.bukkit.inevntory.*;
import team.floracore.bukkit.inevntory.content.*;

public interface InventoryOpener {

    Inventory open(SmartInventory inv, Player player);

    boolean supports(InventoryType type);

    default void fill(Inventory handle, InventoryContents contents) {
        ClickableItem[][] items = contents.all();

        for (int row = 0; row < items.length; row++) {
            for (int column = 0; column < items[row].length; column++) {
                if (items[row][column] != null) {
                    handle.setItem(9 * row + column, items[row][column].getItem());
                }
            }
        }
    }

}
