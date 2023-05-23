package team.floracore.bukkit.util.entity;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

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

	public static NmsContainer getPlayerContainer(Player player) {
		return WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityPlayer.class).getPlayerContainer();
	}

	public static NmsContainer getOpenContainer(Player player) {
		return WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityPlayer.class).getOpenContainer();
	}

	/**
	 * Give item to player
	 *
	 * @param player who you want to give to
	 * @param item   what you want to give
	 * @return true if the player got all the item
	 */
	public static boolean give(Player player, ItemStack item) {
		item = ObcItemStack.asCraftCopy(item).getRaw();
		boolean picked = WrappedObject.wrap(ObcInventory.class, player.getInventory()).getNms().cast(NmsPlayerInventory.class).pickUp(ObcItemStack.ensure(item).getHandle());
		if (picked) {
			float f;
			if (BukkitWrapper.version >= 19) {
				NmsRandomSourceV19 rand = WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityPlayer.class).getRandomV19();
				f = rand.nextFloat() - rand.nextFloat();
			} else {
				Random rand = WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityPlayer.class).getRandomV_19();
				f = rand.nextFloat() - rand.nextFloat();
			}
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, (f * 0.7f + 1.f) * 2.f);
		}
		if (picked && ObcItemStack.isAir(item)) {
			return true;
		} else {
			NmsEntityItem d = WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityPlayer.class).drop(ObcItemStack.ensure(item).getHandle(), false);
			if (!d.isNull()) {
				Item drop = (Item) d.getBukkitEntity();
				drop.setPickupDelay(0);
			}
			return false;
		}
	}
}
