package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagLong",
                                  maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagLong", minVer = 17)})
public interface NmsNBTTagLong extends NmsNBTTag {
    static NmsNBTTagLong newInstance(long value) {
        return WrappedObject.getStatic(NmsNBTTagLong.class).staticNewInstance(value);
    }

    @WrappedConstructor
    NmsNBTTagLong staticNewInstance(long value);

    @Override
    default JsonPrimitive toJson() {
        return new JsonPrimitive(getValue());
    }

    @WrappedBukkitMethod({@VersionName("asLong"), @VersionName(maxVer = 18, value = {"d"}), @VersionName(minVer = 18,
                                                                                                         value = {"e"})})
    long getValue();
}
