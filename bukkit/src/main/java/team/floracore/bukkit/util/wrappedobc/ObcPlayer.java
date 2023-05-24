package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass(@VersionName("obc.entity.CraftPlayer"))
public interface ObcPlayer extends ObcHumanEntity {
    @WrappedMethod("getHandle")
    NmsEntityPlayer getHandle();

    @WrappedBukkitMethod(@VersionName(minVer = 17, value = "getPing"))
    int getPing();
}
