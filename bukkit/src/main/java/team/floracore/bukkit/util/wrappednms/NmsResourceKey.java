package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.ResourceKey", maxVer = 17), @VersionName(value = "net.minecraft.resources.ResourceKey", minVer = 17)})
public interface NmsResourceKey extends WrappedBukkitObject {
	static NmsResourceKey fromKey0V13(NmsMinecraftKey key) {
		return WrappedObject.getStatic(NmsResourceKey.class).staticFromKeyV13(key);
	}

	static NmsResourceKey fromKeyV13(NmsResourceKey rKey, NmsMinecraftKey key) {
		return WrappedObject.getStatic(NmsResourceKey.class).staticFromKeyV13(rKey, key);
	}

	static NmsResourceKey fromKeyV13(NmsMinecraftKey key) {
		return fromKeyV13(fromKey0V13(key), key);
	}

	@WrappedBukkitMethod(@VersionName(value = "a", minVer = 13))
	NmsResourceKey staticFromKeyV13(NmsMinecraftKey key);

	@WrappedBukkitMethod(@VersionName(value = "a", minVer = 13))
	NmsResourceKey staticFromKeyV13(NmsResourceKey rKey, NmsMinecraftKey key);
}
