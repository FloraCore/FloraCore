package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;

@WrappedBukkitClass({@VersionName(value = "nms.InventoryCrafting",
                                  maxVer = 17), @VersionName(value = "net.minecraft.world.inventory.InventoryCrafting",
                                                             minVer = 17)})
public interface NmsInventoryCrafting extends NmsIInventory {
}
