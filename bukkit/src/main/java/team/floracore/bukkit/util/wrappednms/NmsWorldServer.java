package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;

@WrappedBukkitClass({@VersionName(value = "nms.WorldServer", maxVer = 17),
        @VersionName(value = {"net.minecraft.world.level.WorldServer", "net.minecraft.server.level.WorldServer"},
                minVer = 17)})
public interface NmsWorldServer extends NmsWorld {
    @WrappedBukkitFieldAccessor(@VersionName("dimension"))
    int getDimension();
}
