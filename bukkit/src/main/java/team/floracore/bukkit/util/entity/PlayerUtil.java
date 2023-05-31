package team.floracore.bukkit.util.entity;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil {
    public static ItemStack getEquipment(HumanEntity player, EquipmentSlot slot) {
        switch (slot) {
            case CHEST:
                return player.getInventory().getChestplate();
            case FEET:
                return player.getInventory().getBoots();
            case HAND:
                return player.getInventory().getItemInMainHand();
            case HEAD:
                return player.getInventory().getHelmet();
            case LEGS:
                return player.getInventory().getLeggings();
            case OFF_HAND:
                return player.getInventory().getItemInOffHand();
            default:
                throw new IllegalArgumentException("slot " + slot);
        }
    }
}
