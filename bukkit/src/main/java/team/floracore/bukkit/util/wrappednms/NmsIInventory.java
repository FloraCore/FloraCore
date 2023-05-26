package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

import java.util.*;

@WrappedBukkitClass({@VersionName(value = "nms.IInventory",
                                  maxVer = 17), @VersionName(value = "net.minecraft.world.IInventory", minVer = 17)})
public interface NmsIInventory extends WrappedBukkitObject {
    @WrappedBukkitMethod(@VersionName("getOwner"))
    InventoryHolder getOwner();

    @WrappedBukkitMethod(@VersionName("getViewers"))
    List<HumanEntity> getViewers();
}
