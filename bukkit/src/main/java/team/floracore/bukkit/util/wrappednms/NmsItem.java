package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.Item",
        maxVer = 17), @VersionName(value = "net.minecraft.world.item.Item", minVer = 17)})
public interface NmsItem extends WrappedBukkitObject {
    static NmsItem fromId(String id) {
        return fromKey(NmsMinecraftKey.newInstance(id));
    }

    static NmsItem fromKey(NmsMinecraftKey key) {
        if (BukkitWrapper.version >= 14) {
            return NmsIRegistry.getItemsV14().get(key, NmsItem.class);
        } else if (BukkitWrapper.v13) {
            return NmsIRegistry.getItemsV13_14().get(key, NmsItem.class);
        } else {
            return getRegistryV_13().get(key, NmsItem.class);
        }
    }

    static NmsRegistryMaterials getRegistryV_13() {
        return WrappedObject.getStatic(NmsItem.class).staticGetRegistryV_13();
    }

    @WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "REGISTRY"))
    NmsRegistryMaterials staticGetRegistryV_13();

    default String getId() {
        return getKey().toString();
    }

    default NmsMinecraftKey getKey() {
        if (BukkitWrapper.version >= 14) {
            return NmsIRegistry.getItemsV14().getKeyV13(this);
        } else if (BukkitWrapper.v13) {
            return NmsIRegistry.getItemsV13_14().getKey(this);
        } else {
            return getRegistryV_13().getKey(this);
        }
    }

    @WrappedBukkitMethod(@VersionName(value = "b", minVer = 12, maxVer = 13))
    String getNameV12_13(NmsItemStack is);

    @WrappedBukkitFieldAccessor(@VersionName(value = "name", minVer = 8))
    String getNameV8();

    @WrappedBukkitFieldAccessor({@VersionName("maxStackSize"), @VersionName(value = "c",
            minVer = 17,
            maxVer = 18.2f), @VersionName(value = "d",
            minVer = 18.2f)})
    int getMaxStackSize();

    @WrappedBukkitFieldAccessor({@VersionName("maxStackSize"), @VersionName(value = "c",
            minVer = 17,
            maxVer = 18.2f), @VersionName(value = "d",
            minVer = 18.2f)})
    NmsItem setMaxStackSize(int count);
}
