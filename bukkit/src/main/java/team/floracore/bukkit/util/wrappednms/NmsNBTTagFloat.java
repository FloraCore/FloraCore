package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagFloat", maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagFloat", minVer = 17)})
public interface NmsNBTTagFloat extends NmsNBTTag {
    static NmsNBTTagFloat newInstance(float value) {
        return WrappedObject.getStatic(NmsNBTTagFloat.class).staticNewInstance(value);
    }

    @WrappedConstructor
    NmsNBTTagFloat staticNewInstance(float value);

    @WrappedBukkitMethod({@VersionName("asFloat"), @VersionName(maxVer = 17, value = "i"), @VersionName(minVer = 17, value = "j")})
    float getValue();

    @Override
    default JsonPrimitive toJson() {
        return new JsonPrimitive(getValue());
    }
}
