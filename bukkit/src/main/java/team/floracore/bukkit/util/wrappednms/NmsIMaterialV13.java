package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass({@VersionName(value = "nms.IMaterial", minVer = 13, maxVer = 17),
		@VersionName(value = "net.minecraft.world.level.IMaterial", minVer = 17)})
public interface NmsIMaterialV13 extends WrappedBukkitObject {
}
