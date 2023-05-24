package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.Slot", maxVer = 17), @VersionName(value = "net.minecraft.world.inventory.Slot", minVer = 17)})
public interface NmsSlot extends WrappedBukkitObject {

	@WrappedBukkitMethod({@VersionName("isAllowed"), @VersionName(minVer = 18, value = "a")})
	boolean isAllowed(NmsItemStack item);

	default boolean isAllowed(ItemStack item) {
		return isAllowed(ObcItemStack.asNMSCopy(item));
	}

	@WrappedBukkitMethod({@VersionName("isAllowed"), @VersionName(minVer = 18, value = "a")})
	boolean isAllowed(NmsEntityHuman player);

	default boolean isAllowed(HumanEntity player) {
		return isAllowed(WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityHuman.class));
	}

	@WrappedBukkitMethod(@VersionName("a"))
	void onVisit(NmsEntityHuman player, NmsItemStack item);

	default void onVisit(HumanEntity player, ItemStack item) {
		onVisit(WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityHuman.class), ObcItemStack.asNMSCopy(item));
	}

	@WrappedBukkitFieldAccessor({@VersionName("index"), @VersionName(minVer = 17, value = "a")})
	int getIndex();

	@WrappedBukkitFieldAccessor({@VersionName("index"), @VersionName(minVer = 17, value = "a")})
	NmsSlot setIndex(int index);

	@WrappedBukkitFieldAccessor({@VersionName("rawSlotIndex"), @VersionName(minVer = 17, maxVer = 19, value = "d"), @VersionName(minVer = 19, value = "e")})
	int getRawSlot();

	@WrappedBukkitFieldAccessor({@VersionName("rawSlotIndex"), @VersionName(minVer = 17, maxVer = 19, value = "d"), @VersionName(minVer = 19, value = "e")})
	NmsSlot setRawSlot(int index);

	@WrappedBukkitFieldAccessor({@VersionName("inventory"), @VersionName(minVer = 17, maxVer = 19, value = "c"), @VersionName(value = "d", minVer = 19)})
	NmsIInventory getInventory();

	@WrappedBukkitFieldAccessor({@VersionName("inventory"), @VersionName(minVer = 17, maxVer = 19, value = "c"), @VersionName(value = "d", minVer = 19)})
	NmsSlot setInventory(NmsIInventory inv);

	@WrappedBukkitFieldAccessor({@VersionName(maxVer = 14, value = "f"), @VersionName(minVer = 13, maxVer = 19, value = "e"), @VersionName(minVer = 19, value = "f")})
	int getX();

	@WrappedBukkitFieldAccessor({@VersionName(maxVer = 14, value = "f"), @VersionName(minVer = 13, maxVer = 19, value = "e"), @VersionName(minVer = 19, value = "f")})
	NmsSlot setX(int x);

	@WrappedBukkitFieldAccessor({@VersionName(maxVer = 14, value = "g"), @VersionName(minVer = 13, maxVer = 19, value = "f"), @VersionName(minVer = 19, value = "g")})
	int getY();

	@WrappedBukkitFieldAccessor({@VersionName(maxVer = 14, value = "g"), @VersionName(minVer = 13, maxVer = 19, value = "f"), @VersionName(minVer = 19, value = "g")})
	NmsSlot setY(int y);

	@WrappedBukkitMethod(value = {@VersionName("getItem"), @VersionName(minVer = 18, value = "e")})
	NmsItemStack getItem();

	default void setItem(ItemStack item) {
		set(ObcItemStack.asNMSCopy(item));
	}

	default ItemStack getBukkitItem() {
		return ObcItemStack.asBukkitCopy(getItem());
	}

	@WrappedBukkitMethod({@VersionName("set"), @VersionName(minVer = 18, value = "d")})
	void set(NmsItemStack item);
}
