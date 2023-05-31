package team.floracore.bukkit.util.wrappednms;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.Optional;

@Optional
@WrappedBukkitClass(@VersionName(value = "it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap", minVer = 13))
public interface WrappedReference2IntOpenHashMapV13 extends WrappedBukkitObject {
    @Override
    Reference2IntOpenHashMap<Object> getRaw();
}
