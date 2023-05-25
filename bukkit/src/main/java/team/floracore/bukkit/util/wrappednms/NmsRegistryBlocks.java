package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.RegistryBlocks", maxVer = 17), @VersionName(value = "net.minecraft.core.RegistryBlocks", minVer = 17)})
public interface NmsRegistryBlocks extends NmsRegistryMaterials {
    @WrappedBukkitMethod({@VersionName(minVer = 13, value = "getKey"), @VersionName(minVer = 18, value = "b")})
    NmsMinecraftKey getKeyV13(WrappedObject value);

    @WrappedBukkitMethod({@VersionName("get"), @VersionName(value = "a", minVer = 18)})
    WrappedObject get(NmsMinecraftKey key);

    default <T extends WrappedObject> T get(NmsMinecraftKey key, Class<T> wrapper) {
        return get(key).cast(wrapper);
    }
}