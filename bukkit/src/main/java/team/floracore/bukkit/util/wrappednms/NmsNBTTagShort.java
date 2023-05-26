package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagShort",
                                  maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagShort", minVer = 17)})
public interface NmsNBTTagShort extends NmsNBTTag {
    static NmsNBTTagShort newInstance(short value) {
        return WrappedObject.getStatic(NmsNBTTagShort.class).staticNewInstance(value);
    }

    @WrappedConstructor
    NmsNBTTagShort staticNewInstance(short value);

    @Override
    default JsonPrimitive toJson() {
        return new JsonPrimitive(getValue());
    }

    @WrappedBukkitMethod({@VersionName("asShort"), @VersionName(maxVer = 17, value = "f"), @VersionName(minVer = 17,
                                                                                                        value = "g")})
    short getValue();
}
