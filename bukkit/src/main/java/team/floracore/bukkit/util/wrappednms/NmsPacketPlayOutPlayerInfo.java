package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitConstructor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.List;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayOutPlayerInfo", maxVer = 17),
        @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo", minVer = 17)})
public interface NmsPacketPlayOutPlayerInfo extends NmsPacket {
    static NmsPacketPlayOutPlayerInfo newInstance(NmsEnumPlayerInfoAction nmsEnumPlayerInfoAction,
                                                  Iterable nmsEntityPlayers) {
        return WrappedObject.getStatic(NmsPacketPlayOutPlayerInfo.class)
                .staticNewInstance(nmsEnumPlayerInfoAction, nmsEntityPlayers);
    }

    @WrappedBukkitConstructor
    NmsPacketPlayOutPlayerInfo staticNewInstance(NmsEnumPlayerInfoAction nmsEnumPlayerInfoAction,
                                                 Iterable nmsEntityPlayers);

    @WrappedBukkitFieldAccessor(@VersionName("@0"))
    List<NmsPlayerInfoData> getPlayerInfoDataList();
}
