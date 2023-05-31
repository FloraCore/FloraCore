package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;

@WrappedBukkitClass({@VersionName(value = "nms.TileEntitySign",
        maxVer = 17), @VersionName(value = "net.minecraft.world.level.block.entity.TileEntitySign",
        minVer = 17)})

public interface NmsTileEntitySign extends NmsPacket {
    @WrappedBukkitFieldAccessor(@VersionName(value = "isEditable", maxVer = 17))
    void setEditable(boolean editable);
}
