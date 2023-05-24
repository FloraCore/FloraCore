package team.floracore.bukkit.util.wrappednms;


import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.PlayerConnection", maxVer = 17), @VersionName(value = "net.minecraft.server.network.PlayerConnection", minVer = 17)})
public interface NmsPlayerConnection extends NmsPacketListener {
	@WrappedBukkitFieldAccessor({@VersionName("networkManager"), @VersionName(minVer = 17, value = "a", maxVer = 19), @VersionName(value = "b", minVer = 19)})
	NmsNetworkManager getNetworkManager();

	@WrappedBukkitFieldAccessor({@VersionName("player"), @VersionName(minVer = 17, value = "b", maxVer = 19), @VersionName(value = "c", minVer = 19, maxVer = 19.4f), @VersionName(value = "@0", minVer = 19.4f)})
	NmsEntityPlayer getPlayer();

	@WrappedBukkitMethod(@VersionName(minVer = 8, maxVer = 12, value = "sendPacket"))
	void sendPacket(NmsPacket packet);
}
