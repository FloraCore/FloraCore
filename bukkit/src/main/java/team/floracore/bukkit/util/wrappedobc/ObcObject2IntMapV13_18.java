package team.floracore.bukkit.util.wrappedobc;

import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.Object2IntMap;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.Optional;

@Optional
@WrappedBukkitClass(@VersionName(value = "org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.Object2IntMap", minVer = 13))
public interface ObcObject2IntMapV13_18 extends WrappedBukkitObject {
    @Override
    Object2IntMap<Object> getRaw();
}
