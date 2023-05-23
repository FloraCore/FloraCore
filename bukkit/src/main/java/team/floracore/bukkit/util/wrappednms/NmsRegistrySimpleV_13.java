package team.floracore.bukkit.util.wrappednms;


import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

@WrappedBukkitClass(@VersionName(value = "nms.RegistrySimple", maxVer = 13))
public interface NmsRegistrySimpleV_13 extends WrappedBukkitObject {
    @WrappedBukkitFieldAccessor(@VersionName("c"))
    Map<Object, Object> getMap();

    @WrappedBukkitMethod(@VersionName("get"))
    WrappedObject get(WrappedObject key);
}
