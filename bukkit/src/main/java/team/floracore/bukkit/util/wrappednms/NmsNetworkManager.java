package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.nothing.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.nothing.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

@WrappedBukkitClass({@VersionName(value = "nms.NetworkManager", maxVer = 17), @VersionName(value = "net.minecraft.network.NetworkManager", minVer = 17)})
public interface NmsNetworkManager extends WrappedBukkitObject, NothingBukkit {
    static void receivePacketV13(NmsPacket packet, NmsPacketListener listener) {
        WrappedObject.getStatic(NmsNetworkManager.class).staticReceivePacketV13(packet, listener);
    }

    @NothingBukkitInject(name = @VersionName(minVer = 13, value = "a"), args = {NmsPacket.class, NmsPacketListener.class}, location = NothingLocation.FRONT)
    static Optional<Void> beforeReceivePacketV13(@LocalVar(0) NmsPacket packet, @LocalVar(1) NmsPacketListener listener) {
        if (listener.is(NmsPlayerConnection.class)) {
            if (ProtocolUtil.onPacketReceive((Player) listener.cast(NmsPlayerConnection.class).getPlayer().getBukkitEntity(), packet))
                return Nothing.doReturn(null);
        }
        return Nothing.doContinue();
    }

    @WrappedBukkitMethod(@VersionName(minVer = 13, value = "a"))
    void staticReceivePacketV13(NmsPacket packet, NmsPacketListener listener);

    @WrappedBukkitMethod({@VersionName(minVer = 12, value = "sendPacket"), @VersionName(minVer = 18, value = "a")})
    void sendPacket(NmsPacket packet);
}
