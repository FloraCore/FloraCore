package team.floracore.bukkit.inevntory.opener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import team.floracore.bukkit.inevntory.InventoryManager;
import team.floracore.bukkit.inevntory.SmartInventory;

import static com.google.common.base.Strings.lenientFormat;

public class ChestInventoryOpener implements InventoryOpener {
    public static void checkArgument(boolean b, String errorMessageTemplate, int p1) {
        if (!b) {
            throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1));
        }
    }

    @Override
    public Inventory open(SmartInventory inv, Player player) {
        checkArgument(inv.getColumns() == 9,
                      "The column count for the chest inventory must be 9, found: %s.", inv.getColumns());
        checkArgument(inv.getRows() >= 1 && inv.getRows() <= 6,
                      "The row count for the chest inventory must be between 1 and 6, found: %s", inv.getRows());
        InventoryManager manager = inv.getManager();
        Inventory handle = Bukkit.createInventory(player, inv.getRows() * inv.getColumns(), inv.getTitle());

        fill(handle, manager.getContents(player).get());
        player.openInventory(handle);
        return handle;
    }

    @Override
    public boolean supports(InventoryType type) {
        return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST;
    }
}
