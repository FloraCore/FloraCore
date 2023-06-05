package team.floracore.bukkit.util.wrappednms;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.IChatBaseComponent", maxVer = 17),
        @VersionName(value = "net.minecraft.network.chat.IChatBaseComponent", minVer = 17)})
public interface NmsIChatBaseComponent extends WrappedBukkitObject {

    @WrappedBukkitClass({@VersionName(value = "nms.IChatBaseComponent$ChatSerializer",
            maxVer = 17), @VersionName(value = "net.minecraft.network.chat.IChatBaseComponent$ChatSerializer",
            minVer = 17)})
    interface NmsChatSerializer extends WrappedBukkitObject {
        static NmsIChatBaseComponent jsonToComponent(String json) {
            return WrappedObject.getStatic(NmsChatSerializer.class).staticJsonToComponent(json);
        }

        static String toJson(NmsIChatBaseComponent s) {
            return WrappedObject.getStatic(NmsChatSerializer.class).staticToJson(s);
        }

        static String getJson(String text) {
            JsonObject o = new JsonObject();
            o.add("text", new JsonPrimitive(text));
            return o.toString();
        }

        @WrappedMethod({"jsonToComponent", "a"})
        NmsIChatBaseComponent staticJsonToComponent(String json);

        @WrappedMethod({"a"})
        String staticToJson(NmsIChatBaseComponent s);
    }
}
