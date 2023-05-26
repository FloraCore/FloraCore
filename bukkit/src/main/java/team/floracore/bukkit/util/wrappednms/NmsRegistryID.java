package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

import java.util.*;

@WrappedBukkitClass({@VersionName(value = "nms.RegistryID",
                                  maxVer = 17), @VersionName(value = "net.minecraft.util.RegistryID", minVer = 17)})
public interface NmsRegistryID extends WrappedBukkitObject {
    @WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "b"))
    Object[] getBV_13();

    @WrappedBukkitFieldAccessor(@VersionName(minVer = 12, maxVer = 13, value = "c"))
    int[] getCV12_13();

    default int[] getCV8_() {
        Integer[] t = getAV8_().values().parallelStream().toArray(Integer[]::new);
        int[] r = new int[t.length];
        for (int i = 0; i < t.length; i++) {
            r[i] = t[i];
        }
        return r;

    }

    @WrappedBukkitFieldAccessor(@VersionName(minVer = 8, value = "a"))
    IdentityHashMap<?, Integer> getAV8_();

    @WrappedBukkitFieldAccessor(@VersionName(minVer = 12, maxVer = 13, value = "d"))
    Object[] getDV12_13();

    @WrappedBukkitMethod(@VersionName(value = "getId", minVer = 12, maxVer = 13))
    int getIdV12_13(Object o);

    @WrappedBukkitMethod(@VersionName(value = "e", minVer = 13))
    int eV13(int i);

    @WrappedBukkitMethod(@VersionName(value = "d", minVer = 13))
    int dV13(Object o);
}
