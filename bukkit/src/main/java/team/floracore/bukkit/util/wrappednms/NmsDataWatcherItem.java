package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.DataWatcher$Item", maxVer = 17), @VersionName(value = "net.minecraft.network.syncher.DataWatcher$Item", minVer = 17)})
public interface NmsDataWatcherItem extends WrappedBukkitObject {
	static NmsDataWatcherItem newInstance(NmsDataWatcherObject type, Object value) {
		return WrappedObject.getStatic(NmsDataWatcherItem.class).staticNewInstance(type, value);
	}

	@WrappedMethod("a")
	NmsDataWatcherObject getType();

	@WrappedMethod("b")
	Object getValue();

	default <R extends WrappedObject> R getValue(Class<R> wrapper) {
		return WrappedObject.wrap(wrapper, getValue());
	}

	@WrappedConstructor
	NmsDataWatcherItem staticNewInstance(NmsDataWatcherObject type, Object value);
}
