package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsEntityPlayer;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.common.util.wrapper.WrappedMethod;

@WrappedBukkitClass(@VersionName("obc.entity.CraftPlayer"))
public interface ObcPlayer extends ObcHumanEntity {
    @WrappedMethod("getHandle")
    NmsEntityPlayer getHandle();

    @WrappedBukkitMethod(@VersionName(minVer = 17, value = "getPing"))
    int getPing();
}
