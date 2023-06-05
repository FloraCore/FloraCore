package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

/**
 * WorldData
 *
 * @author xLikeWATCHDOG
 */
@WrappedBukkitClass(@VersionName(maxVer = 17, value = "nms.WorldData"))
public interface NmsWorldData extends WrappedBukkitObject {
    @WrappedBukkitMethod(@VersionName("getDifficulty"))
    NmsEnumDifficulty getDifficulty();

    @WrappedBukkitMethod(@VersionName("getType"))
    NmsWorldType getType();
}
