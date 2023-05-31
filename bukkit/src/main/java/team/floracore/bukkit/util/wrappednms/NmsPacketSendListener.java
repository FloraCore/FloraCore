package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass(@VersionName(value = "net.minecraft.network.PacketSendListener", minVer = 19.1f))
public interface NmsPacketSendListener extends WrappedBukkitObject {
}
