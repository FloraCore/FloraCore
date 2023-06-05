package team.floracore.bukkit.util.wrappednms;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass(@VersionName(value = "it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap", minVer = 13))
public interface WrappedObject2ObjectLinkedOpenHashMapV13 extends WrappedBukkitObject {
    @Override
    Object2ObjectLinkedOpenHashMap<Object, Object> getRaw();
}
