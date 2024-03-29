package team.floracore.bukkit.util.wrappednms;

import org.bukkit.Location;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitConstructor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.BlockPosition", maxVer = 17),
		@VersionName(value = "net.minecraft.core.BlockPosition", minVer = 17)})
public interface NmsBlockPosition extends WrappedBukkitObject {
	static NmsBlockPosition newInstance(Location location) {
		return newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	static NmsBlockPosition newInstance(int x, int y, int z) {
		return WrappedObject.getStatic(NmsBlockPosition.class).staticNewInstance(x, y, z);
	}

	@WrappedBukkitConstructor
	NmsBlockPosition staticNewInstance(int x, int y, int z);

}
