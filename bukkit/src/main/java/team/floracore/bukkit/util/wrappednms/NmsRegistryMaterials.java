package team.floracore.bukkit.util.wrappednms;

import com.google.common.collect.BiMap;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappedobc.ObcObject2IntMapV13_18;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.Optional;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.Map;

@WrappedBukkitClass({@VersionName(value = "nms.RegistryMaterials", maxVer = 17),
        @VersionName(value = "net.minecraft.core.RegistryMaterials", minVer = 17)})
public interface NmsRegistryMaterials extends WrappedBukkitObject {
    default NmsMinecraftKey getKey(WrappedObject value) {
        if (BukkitWrapper.v13) {
            return getKeyV13(value);
        } else {
            return getKeyV_13(value).cast(NmsMinecraftKey.class);
        }
    }

    @WrappedBukkitMethod({@VersionName(value = "getKey", minVer = 13), @VersionName(minVer = 18, value = "b")})
    NmsMinecraftKey getKeyV13(WrappedObject value);

    @WrappedBukkitMethod(@VersionName(value = "b", maxVer = 13))
    WrappedObject getKeyV_13(WrappedObject value);

    default <T extends WrappedObject> T get(NmsMinecraftKey key, Class<T> wrapper) {
        return get(key).cast(wrapper);
    }

    default WrappedObject get(NmsMinecraftKey key) {
        if (BukkitWrapper.v13) {
            return getV13(key);
        } else {
            return getV_13(key);
        }
    }

    @WrappedBukkitMethod({@VersionName(value = "get", minVer = 13), @VersionName(value = "a", minVer = 18)})
    WrappedObject getV13(NmsMinecraftKey key);

    @WrappedBukkitMethod(@VersionName(value = "get", maxVer = 13))
    WrappedObject getV_13(WrappedObject key);

    default Map<Object, Object> getKeyMapV13() {
        if (BukkitWrapper.version >= 18.2) {
            return getKeyMapV182();
        } else {
            return getKeyMapV13_182();
        }
    }

    @WrappedBukkitFieldAccessor(@VersionName(value = "@0", minVer = 18.2f))
    Map<Object, Object> getKeyMapV182();

    @WrappedBukkitFieldAccessor(@VersionName(value = "@0", minVer = 13, maxVer = 18.2f))
    BiMap<Object, Object> getKeyMapV13_182();

    @WrappedBukkitFieldAccessor({@VersionName(maxVer = 13, value = "a"), @VersionName(minVer = 13,
            maxVer = 16,
            value = "b")})
    NmsRegistryID getRegIDV_15();

    default Map<Object, Integer> getIdMapV16() {
        try {
            return getIdMapV16Paper().getRaw();
        } catch (Throwable e) {
            if (BukkitWrapper.version >= 18) {
                return getIdMapV18_Paper().getRaw();
            } else {
                return getIdMapV16_18_Paper().getRaw();
            }
        }
    }

    @Optional
    @WrappedBukkitFieldAccessor(@VersionName(minVer = 16, value = "@0"))
    WrappedReference2IntOpenHashMapV13 getIdMapV16Paper();

    @Optional
    @WrappedBukkitFieldAccessor(@VersionName(minVer = 18, value = "@0"))
    WrappedObject2IntMapV18 getIdMapV18_Paper();

    @Optional
    @WrappedBukkitFieldAccessor(@VersionName(minVer = 16, value = "@0", maxVer = 18))
    ObcObject2IntMapV13_18 getIdMapV16_18_Paper();

    default Map<Object, Object> getResourceKeyMapV16() {
        if (BukkitWrapper.version >= 18.2f) {
            return getResourceKeyMapV182();
        } else {
            return getResourceKeyMapV16_182();
        }
    }

    @WrappedBukkitFieldAccessor(@VersionName(minVer = 18.2f, value = "@1"))
    Map<Object, Object> getResourceKeyMapV182();

    @WrappedBukkitFieldAccessor(@VersionName(minVer = 16, value = "@1", maxVer = 18.2f))
    BiMap<Object, Object> getResourceKeyMapV16_182();

    @WrappedBukkitFieldAccessor({@VersionName(minVer = 16, value = "@0", maxVer = 18.2f), @VersionName(minVer = 18.2f,
            value = "@3")})
    Map<Object, Object> getLifecycleMapV16();

    @WrappedBukkitMethod(@VersionName("@0"))
    int getId(WrappedObject obj);

    default <T extends WrappedObject> T fromId(int id, Class<T> wrapper) {
        return fromId(id).cast(wrapper);
    }

    @WrappedBukkitMethod(@VersionName({"fromId", "@0"}))
    WrappedObject fromId(int id);
}
