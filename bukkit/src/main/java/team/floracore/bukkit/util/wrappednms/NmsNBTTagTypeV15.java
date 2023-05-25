package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.io.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagType", minVer = 15, maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagType", minVer = 17)})
public interface NmsNBTTagTypeV15 extends WrappedBukkitObject {
    @WrappedMethod("b")
    NmsNBTBase read(DataInput s, int depth, NmsNBTReadLimiter limiter);
}
