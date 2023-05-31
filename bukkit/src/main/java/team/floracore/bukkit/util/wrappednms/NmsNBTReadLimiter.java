package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedConstructor;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.NBTReadLimiter",
                                  maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTReadLimiter", minVer = 17)})
public interface NmsNBTReadLimiter extends WrappedBukkitObject {
    static NmsNBTReadLimiter newInstance(long l) {
        return WrappedObject.getStatic(NmsNBTReadLimiter.class).staticNewInstance(l);
    }

    @WrappedConstructor
    NmsNBTReadLimiter staticNewInstance(long l);
}
