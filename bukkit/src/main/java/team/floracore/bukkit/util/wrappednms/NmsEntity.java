package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.Entity", maxVer = 17), @VersionName(value = "net.minecraft.world.entity.Entity", minVer = 17)})
public interface NmsEntity extends NmsICommandListener {
	static NmsEntity newInstanceV13(NmsEntityTypes type, NmsWorld world) {
		return WrappedObject.wrap(NmsEntity.class, null).staticNewInstanceV13(type, world);
	}

	static NmsEntity newInstanceV12_13(NmsWorld world) {
		return WrappedObject.wrap(NmsEntity.class, null).staticNewInstanceV12_13(world);
	}

	@WrappedBukkitConstructor(minVer = 13)
	NmsEntity staticNewInstanceV13(NmsEntityTypes type, NmsWorld world);

	@WrappedBukkitConstructor(minVer = 12, maxVer = 13)
	NmsEntity staticNewInstanceV12_13(NmsWorld world);

	@WrappedMethod("getBukkitEntity")
	Entity getBukkitEntity();
}
