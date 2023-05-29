package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.util.function.*;

@WrappedBukkitClass({@VersionName(value = "nms.EntityTypes",
                                  maxVer = 17), @VersionName(value = "net.minecraft.world.entity.EntityTypes",
                                                             minVer = 17)})
public interface NmsEntityTypes extends WrappedBukkitObject {
    static NmsEntity spawn(NmsNBTTagCompound nbt, NmsWorld world) {
        if (BukkitWrapper.v17) {
            return WrappedObject.getStatic(NmsEntityTypes.class).staticSpawnV17(nbt, world, e -> e);
        } else {
            return WrappedObject.getStatic(NmsEntityTypes.class).staticSpawnV_17(nbt, world);
        }
    }

    static String getTranslateKeyV_13(NmsEntity entity) {
        return "entity." + getNameV_13(entity) + ".name";
    }

    static String getNameV_13(NmsEntity entity) {
        return WrappedObject.getStatic(NmsEntityTypes.class).staticGetNameV_13(entity);
    }

    static NmsRegistryMaterials getEntityTypesV_13() {
        return WrappedObject.getStatic(NmsEntityTypes.class).staticGetEntityTypesV_13();
    }

    @WrappedBukkitMethod(@VersionName(value = "a", minVer = 17))
    NmsEntity staticSpawnV17(NmsNBTTagCompound nbt, NmsWorld world, Function<Object, Object> entityProcessor);

    @WrappedBukkitMethod(@VersionName(value = "a", maxVer = 17))
    NmsEntity staticSpawnV_17(NmsNBTTagCompound nbt, NmsWorld world);

    @WrappedBukkitMethod(@VersionName(value = "b", maxVer = 13))
    String staticGetNameV_13(NmsEntity entity);

    @WrappedBukkitMethod(@VersionName(value = "@0", maxVer = 13))
    NmsRegistryMaterials staticGetEntityTypesV_13();
}
