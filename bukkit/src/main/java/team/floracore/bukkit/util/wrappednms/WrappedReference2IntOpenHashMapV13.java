package team.floracore.bukkit.util.wrappednms;

import it.unimi.dsi.fastutil.objects.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.*;

@Optional
@WrappedBukkitClass(@VersionName(value = "it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap", minVer = 13))
public interface WrappedReference2IntOpenHashMapV13 extends WrappedBukkitObject {
	@Override
	Reference2IntOpenHashMap<Object> getRaw();
}
