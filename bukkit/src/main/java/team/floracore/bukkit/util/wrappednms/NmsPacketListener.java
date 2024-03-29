package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass({@VersionName(value = "nms.PacketListener", maxVer = 17),
		@VersionName(value = "net.minecraft.network.PacketListener", minVer = 17)})
public interface NmsPacketListener extends WrappedBukkitObject {
}
