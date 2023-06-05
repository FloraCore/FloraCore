package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedConstructor;
import team.floracore.common.util.wrapper.WrappedMethod;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.DataWatcher$Item", maxVer = 17),
        @VersionName(value = "net.minecraft.network.syncher.DataWatcher$Item", minVer = 17)})
public interface NmsDataWatcherItem extends WrappedBukkitObject {
    static NmsDataWatcherItem newInstance(NmsDataWatcherObject type, Object value) {
        return WrappedObject.getStatic(NmsDataWatcherItem.class).staticNewInstance(type, value);
    }

    @WrappedConstructor
    NmsDataWatcherItem staticNewInstance(NmsDataWatcherObject type, Object value);

    @WrappedMethod("a")
    NmsDataWatcherObject getType();

    default <R extends WrappedObject> R getValue(Class<R> wrapper) {
        return WrappedObject.wrap(wrapper, getValue());
    }

    @WrappedMethod("b")
    Object getValue();
}
