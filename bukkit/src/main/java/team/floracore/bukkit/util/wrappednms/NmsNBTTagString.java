package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagString", maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagString", minVer = 17)})
public interface NmsNBTTagString extends NmsNBTTag {
    static NmsNBTTagString newInstance(String value) {
        return WrappedObject.getStatic(NmsNBTTagString.class).staticNewInstance(value);
    }

    @WrappedConstructor
    NmsNBTTagString staticNewInstance(String value);

    @Override
    default JsonPrimitive toJson() {
        return new JsonPrimitive(getValue());
    }

    @WrappedFieldAccessor("@0")
    String getValue();
}
