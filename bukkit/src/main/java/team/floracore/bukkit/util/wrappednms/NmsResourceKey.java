package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.ResourceKey", maxVer = 17),
		@VersionName(value = "net.minecraft.resources.ResourceKey", minVer = 17)})
public interface NmsResourceKey extends WrappedBukkitObject {
	static NmsResourceKey fromKeyV13(NmsMinecraftKey key) {
		return fromKeyV13(fromKey0V13(key), key);
	}

	static NmsResourceKey fromKeyV13(NmsResourceKey rKey, NmsMinecraftKey key) {
		return WrappedObject.getStatic(NmsResourceKey.class).staticFromKeyV13(rKey, key);
	}

	static NmsResourceKey fromKey0V13(NmsMinecraftKey key) {
		return WrappedObject.getStatic(NmsResourceKey.class).staticFromKeyV13(key);
	}

	@WrappedBukkitMethod(@VersionName(value = "a", minVer = 13))
	NmsResourceKey staticFromKeyV13(NmsResourceKey rKey, NmsMinecraftKey key);

	@WrappedBukkitMethod(@VersionName(value = "a", minVer = 13))
	NmsResourceKey staticFromKeyV13(NmsMinecraftKey key);
}
