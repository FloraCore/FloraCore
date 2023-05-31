package team.floracore.bukkit.inevntory.opener;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import team.floracore.bukkit.inevntory.ClickableItem;
import team.floracore.bukkit.inevntory.SmartInventory;
import team.floracore.bukkit.inevntory.content.InventoryContents;

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
