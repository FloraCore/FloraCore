package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayOutPlayerInfo", maxVer = 17), @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo", minVer = 17)})
public interface NmsPacketPlayOutPlayerInfo extends NmsPacket {

}
