package team.floracore.bukkit.util.wrappednms;

import com.google.gson.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.MojangsonParser", maxVer = 17), @VersionName(value = "net.minecraft.nbt.MojangsonParser", minVer = 17)})
public interface NmsMojangsonParser extends WrappedBukkitObject {
	static NmsNBTTagCompound parse(String json) {
		return WrappedObject.getStatic(NmsMojangsonParser.class).staticParse(json);
	}

	static JsonObject parseNonstandardJson(String json) {
		return parse(json).toJson();
	}

	@WrappedBukkitMethod({@VersionName("parse"), @VersionName(minVer = 18, value = "a")})
	NmsNBTTagCompound staticParse(String json);
}
