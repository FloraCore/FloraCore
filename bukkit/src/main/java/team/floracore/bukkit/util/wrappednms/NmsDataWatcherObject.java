package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.Optional;

@Optional
@WrappedBukkitClass({@VersionName(value = "nms.DataWatcherObject",
                                  maxVer = 17), @VersionName(value = "net.minecraft.network.syncher.DataWatcherObject",
                                                             minVer = 17)})
public interface NmsDataWatcherObject extends WrappedBukkitObject {
}
