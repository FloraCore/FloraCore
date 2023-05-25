package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayOutPlayerInfo", maxVer = 17), @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo", minVer = 17)})
public interface NmsPacketPlayOutPlayerInfo extends NmsPacket {
    static NmsPacketPlayOutPlayerInfo newInstance(NmsEnumPlayerInfoAction nmsEnumPlayerInfoAction,
                                                  Iterable nmsEntityPlayers) {
        return WrappedObject.getStatic(NmsPacketPlayOutPlayerInfo.class).staticNewInstance(nmsEnumPlayerInfoAction, nmsEntityPlayers);
    }

    @WrappedBukkitConstructor
    NmsPacketPlayOutPlayerInfo staticNewInstance(NmsEnumPlayerInfoAction nmsEnumPlayerInfoAction,
                                                 Iterable nmsEntityPlayers);
}
