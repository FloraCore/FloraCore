package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;

@WrappedBukkitClass({@VersionName(value = "nms.EntityPlayer", maxVer = 17),
        @VersionName(value = "net.minecraft.server.level.EntityPlayer", minVer = 17)})
public interface NmsEntityPlayer extends NmsEntityHuman {
    @WrappedBukkitFieldAccessor({@VersionName("playerConnection"), @VersionName(minVer = 17, value = "b")})
    NmsPlayerConnection getPlayerConnection();

    @WrappedBukkitFieldAccessor(@VersionName("ping"))
    int getPing();
}
