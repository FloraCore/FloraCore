package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.*;

@Optional
@WrappedBukkitClass({@VersionName(value = "nms.DataWatcherObject",
                                  maxVer = 17), @VersionName(value = "net.minecraft.network.syncher.DataWatcherObject",
                                                             minVer = 17)})
public interface NmsDataWatcherObject extends WrappedBukkitObject {
}
