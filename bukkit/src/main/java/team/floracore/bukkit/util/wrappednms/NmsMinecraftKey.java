package team.floracore.bukkit.util.wrappednms;

import org.bukkit.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

@WrappedBukkitClass({@VersionName(value = "nms.MinecraftKey", maxVer = 17), @VersionName(value = "net.minecraft.resources.MinecraftKey", minVer = 17)})
public interface NmsMinecraftKey extends WrappedBukkitObject {
	static NmsMinecraftKey newInstance(String namespace, String key) {
		return WrappedObject.getStatic(NmsMinecraftKey.class).staticNewInstance(namespace, key);
	}

	static NmsMinecraftKey newInstance(String s) {
		return WrappedObject.getStatic(NmsMinecraftKey.class).staticNewInstance(s);
	}

	static NmsMinecraftKey newInstance(NamespacedKey key) {
		return newInstance(key.getNamespace(), key.getKey());
	}

	static NmsMinecraftKey random() {
		return NmsMinecraftKey.newInstance("mzlib", UUID.randomUUID().toString().replace("-", ""));
	}

	@WrappedConstructor
	NmsMinecraftKey staticNewInstance(String namespace, String key);

	@WrappedConstructor
	NmsMinecraftKey staticNewInstance(String s);

	@WrappedBukkitFieldAccessor(@VersionName({"namespace", "@0"}))
	String getNamespace();

	@WrappedBukkitFieldAccessor(@VersionName({"key", "@1"}))
	String getKey();

	@SuppressWarnings("deprecation")
	default NamespacedKey toBukkit() {
		return new NamespacedKey(getNamespace(), getKey());
	}
}
