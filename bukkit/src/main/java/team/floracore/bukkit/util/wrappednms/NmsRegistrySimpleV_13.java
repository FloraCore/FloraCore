package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.Map;

@WrappedBukkitClass(@VersionName(value = "nms.RegistrySimple", maxVer = 13))
public interface NmsRegistrySimpleV_13 extends WrappedBukkitObject {
    @WrappedBukkitFieldAccessor(@VersionName("c"))
    Map<Object, Object> getMap();

    @WrappedBukkitMethod(@VersionName("get"))
    WrappedObject get(WrappedObject key);
}
