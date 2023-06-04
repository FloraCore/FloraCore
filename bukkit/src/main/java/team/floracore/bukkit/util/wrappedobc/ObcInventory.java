package team.floracore.bukkit.util.wrappedobc;

import org.bukkit.inventory.Inventory;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsIInventory;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;

@WrappedBukkitClass(@VersionName("obc.inventory.CraftInventory"))
public interface ObcInventory extends WrappedBukkitObject {
	@WrappedMethod("getInventory")
	NmsIInventory getNms();

	@Override
	Inventory getRaw();
}
