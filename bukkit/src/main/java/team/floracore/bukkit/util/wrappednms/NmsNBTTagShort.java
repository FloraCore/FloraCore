package team.floracore.bukkit.util.wrappednms;

import com.google.gson.JsonPrimitive;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.common.util.wrapper.WrappedConstructor;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagShort", maxVer = 17),
		@VersionName(value = "net.minecraft.nbt.NBTTagShort", minVer = 17)})
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

	@WrappedBukkitMethod({@VersionName("asShort"),
			@VersionName(maxVer = 17, value = "f"),
			@VersionName(minVer = 17, value = "g")})
	short getValue();
}
