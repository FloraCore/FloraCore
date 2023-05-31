package team.floracore.bukkit.util.wrappedmojang;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass(@VersionName(value = "com.mojang.serialization.Lifecycle", minVer = 16))
public interface WrappedLifecycleV16 extends WrappedBukkitObject {
}
