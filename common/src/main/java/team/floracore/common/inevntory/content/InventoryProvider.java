package team.floracore.common.inevntory.content;

import org.bukkit.entity.*;

public interface InventoryProvider {
    void init(Player player, InventoryContents contents);

    default void update(Player player, InventoryContents contents) {
    }
}
