package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTReadLimiter", maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTReadLimiter", minVer = 17)})
public interface NmsNBTReadLimiter extends WrappedBukkitObject {
	static NmsNBTReadLimiter newInstance(long l) {
		return WrappedObject.getStatic(NmsNBTReadLimiter.class).staticNewInstance(l);
	}

	@WrappedConstructor
	NmsNBTReadLimiter staticNewInstance(long l);
}
