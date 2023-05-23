package team.floracore.bukkit.util.wrappednms;


import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.IChatBaseComponent", maxVer = 17), @VersionName(value = "net.minecraft.network.chat.IChatBaseComponent", minVer = 17)})
public interface NmsIChatBaseComponent extends WrappedBukkitObject {
	@WrappedBukkitClass({@VersionName(value = "nms.IChatBaseComponent$ChatSerializer", maxVer = 17), @VersionName(value = "net.minecraft.network.chat.IChatBaseComponent$ChatSerializer", minVer = 17)})
	interface NmsChatSerializer extends WrappedBukkitObject {
		static NmsIChatBaseComponent jsonToComponent(String json) {
			return WrappedObject.getStatic(NmsChatSerializer.class).staticJsonToComponent(json);
		}

		static String toJson(NmsIChatBaseComponent s) {
			return WrappedObject.getStatic(NmsChatSerializer.class).staticToJson(s);
		}

		@WrappedMethod({"jsonToComponent", "a"})
		NmsIChatBaseComponent staticJsonToComponent(String json);

		@WrappedMethod({"a"})
		String staticToJson(NmsIChatBaseComponent s);
	}
}
