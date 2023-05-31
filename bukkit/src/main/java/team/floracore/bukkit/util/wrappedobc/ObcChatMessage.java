package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsIChatBaseComponent;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass(@VersionName("obc.util.CraftChatMessage"))
public interface ObcChatMessage extends WrappedBukkitObject {
    static String fromStringOrNullToJSONV13(String s) {
        if (BukkitWrapper.v17) {
            return fromStringOrNullToJSONV17(s);
        } else {
            return toJson(fromStringOrNullV13(s));
        }
    }

    static String fromStringOrNullToJSONV17(String s) {
        return WrappedObject.getStatic(ObcChatMessage.class).staticFromStringOrNullToJSONV17(s);
    }

    static String toJson(NmsIChatBaseComponent s) {
        return NmsIChatBaseComponent.NmsChatSerializer.toJson(s);
    }

    static NmsIChatBaseComponent fromStringOrNullV13(String s) {
        return WrappedObject.getStatic(ObcChatMessage.class).staticFromStringOrNullV13(s);
    }

    static String fromJSONComponentV13(String json) {
        if (BukkitWrapper.v17) {
            return fromJSONComponentV17(json);
        } else {
            return fromComponentV13(NmsIChatBaseComponent.NmsChatSerializer.jsonToComponent(json));
        }
    }

    static String fromJSONComponentV17(String s) {
        return WrappedObject.getStatic(ObcChatMessage.class).staticFromJSONComponentV17(s);
    }

    static String fromComponentV13(NmsIChatBaseComponent cc) {
        return WrappedObject.getStatic(ObcChatMessage.class).staticFromComponentV13(cc);
    }

    @WrappedBukkitMethod(@VersionName(value = "fromStringOrNullToJSON", minVer = 17))
    String staticFromStringOrNullToJSONV17(String s);

    @WrappedBukkitMethod(@VersionName(value = "fromStringOrNull", minVer = 13))
    NmsIChatBaseComponent staticFromStringOrNullV13(String s);

    @WrappedBukkitMethod(@VersionName(value = "fromJSONComponent", minVer = 17))
    String staticFromJSONComponentV17(String s);

    @WrappedBukkitMethod(@VersionName(value = "fromComponent", minVer = 13))
    String staticFromComponentV13(NmsIChatBaseComponent cc);
}
