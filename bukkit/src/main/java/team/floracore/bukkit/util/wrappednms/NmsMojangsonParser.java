package team.floracore.bukkit.util.wrappednms;

import com.google.gson.JsonObject;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.MojangsonParser", maxVer = 17),
        @VersionName(value = "net.minecraft.nbt.MojangsonParser", minVer = 17)})
public interface NmsMojangsonParser extends WrappedBukkitObject {
    static JsonObject parseNonstandardJson(String json) {
        return parse(json).toJson();
    }

    static NmsNBTTagCompound parse(String json) {
        return WrappedObject.getStatic(NmsMojangsonParser.class).staticParse(json);
    }

    @WrappedBukkitMethod({@VersionName("parse"), @VersionName(minVer = 18, value = "a")})
    NmsNBTTagCompound staticParse(String json);
}
