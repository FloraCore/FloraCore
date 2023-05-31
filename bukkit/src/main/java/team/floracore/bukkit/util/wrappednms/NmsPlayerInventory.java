package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;

@WrappedBukkitClass({@VersionName(value = "nms.PlayerInventory",
                                  maxVer = 17), @VersionName(value = "net.minecraft.world.entity.player.PlayerInventory",
                                                             minVer = 17)})
public interface NmsPlayerInventory extends NmsIInventory {
    @WrappedBukkitMethod({@VersionName("pickup"), @VersionName(value = "@0", minVer = 17)})
    boolean pickUp(NmsItemStack item);
}
