package team.floracore.bukkit.util.wrappednms;

import it.unimi.dsi.fastutil.objects.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass(@VersionName(value = "it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap", minVer = 13))
public interface WrappedObject2ObjectLinkedOpenHashMapV13 extends WrappedBukkitObject {
    @Override
    Object2ObjectLinkedOpenHashMap<Object, Object> getRaw();
}
