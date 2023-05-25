package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagByte", maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagByte", minVer = 17)})
public interface NmsNBTTagByte extends NmsNBTTag {
    static NmsNBTTagByte newInstance(byte value) {
        return WrappedObject.getStatic(NmsNBTTagByte.class).staticNewInstance(value);
    }

    @WrappedConstructor
    NmsNBTTagByte staticNewInstance(byte value);

    @Override
    default JsonPrimitive toJson() {
        return new JsonPrimitive(getValue());
    }

    @WrappedBukkitMethod({@VersionName("asByte"), @VersionName(value = "g", maxVer = 13), @VersionName(value = "h", minVer = 18)})
    byte getValue();
}
