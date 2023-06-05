package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

/**
 * PlayerInteractManager类
 *
 * @author xLikeWATCHDOG
 */
@WrappedBukkitClass(@VersionName(maxVer = 17, value = "nms.PlayerInteractManager"))
public interface NmsPlayerInteractManager extends WrappedBukkitObject {
    @WrappedBukkitMethod(@VersionName("getGameMode"))
    NmsEnumGamemode getGameMode();
}
