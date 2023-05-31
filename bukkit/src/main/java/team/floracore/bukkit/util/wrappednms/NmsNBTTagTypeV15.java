package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;

import java.io.DataInput;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagType",
        minVer = 15,
        maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagType", minVer = 17)})
public interface NmsNBTTagTypeV15 extends WrappedBukkitObject {
    @WrappedMethod("b")
    NmsNBTBase read(DataInput s, int depth, NmsNBTReadLimiter limiter);
}
