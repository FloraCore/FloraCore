package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagDouble",
                                  maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagDouble", minVer = 17)})
public interface NmsNBTTagDouble extends NmsNBTTag {
    static NmsNBTTagDouble newInstance(double value) {
        return WrappedObject.getStatic(NmsNBTTagDouble.class).staticNewInstance(value);
    }

    @WrappedConstructor
    NmsNBTTagDouble staticNewInstance(double value);

    default Double getValue() {
        return getValue0();
    }

    @WrappedBukkitMethod({@VersionName("asDouble"), @VersionName(maxVer = 17, value = "@0"), @VersionName(minVer = 17,
                                                                                                          value = "@0")})
    double getValue0();

    @Override
    default JsonPrimitive toJson() {
        return new JsonPrimitive(getValue0());
    }
}
