package team.floracore.bukkit.util.wrappednms;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass(@VersionName(value = "it.unimi.dsi.fastutil.objects.Object2IntMap", minVer = 18))
public interface WrappedObject2IntMapV18 extends WrappedBukkitObject {
	@Override
	Object2IntMap<Object> getRaw();
}
