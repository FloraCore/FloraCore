package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass(@VersionName(value = "net.minecraft.util.RandomSource", minVer = 19))
public interface NmsRandomSourceV19 extends WrappedBukkitObject {
    @WrappedBukkitMethod(@VersionName("@0"))
    float nextFloat();
}
