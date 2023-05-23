package team.floracore.bukkit.util.wrappednms;

import it.unimi.dsi.fastutil.objects.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass(@VersionName(value = "it.unimi.dsi.fastutil.objects.Object2IntMap", minVer = 18))
public interface WrappedObject2IntMapV18 extends WrappedBukkitObject {
	@Override
	Object2IntMap<Object> getRaw();
}
