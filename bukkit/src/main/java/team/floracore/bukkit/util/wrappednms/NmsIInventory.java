package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

import java.util.List;

@WrappedBukkitClass({@VersionName(value = "nms.IInventory", maxVer = 17),
		@VersionName(value = "net.minecraft.world.IInventory", minVer = 17)})
public interface NmsIInventory extends WrappedBukkitObject {
	@WrappedBukkitMethod(@VersionName("getOwner"))
	InventoryHolder getOwner();

	@WrappedBukkitMethod(@VersionName("getViewers"))
	List<HumanEntity> getViewers();
}
