package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagInt", maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagInt", minVer = 17)})
public interface NmsNBTTagInt extends NmsNBTTag {
    static NmsNBTTagInt newInstance(int value) {
        return WrappedObject.getStatic(NmsNBTTagInt.class).staticNewInstance(value);
    }

    @WrappedConstructor
    NmsNBTTagInt staticNewInstance(int value);

    @Override
    default JsonPrimitive toJson() {
        return new JsonPrimitive(getValue());
    }

    @WrappedBukkitFieldAccessor(@VersionName("@0"))
    int getValue();
}
