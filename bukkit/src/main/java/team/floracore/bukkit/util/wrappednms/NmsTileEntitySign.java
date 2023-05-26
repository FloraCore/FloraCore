package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.TileEntitySign",
                                  maxVer = 17), @VersionName(value = "net.minecraft.world.level.block.entity.TileEntitySign",
                                                             minVer = 17)})

public interface NmsTileEntitySign extends NmsPacket {
    @WrappedBukkitFieldAccessor(@VersionName(value = "isEditable", maxVer = 17))
    void setEditable(boolean editable);
}
