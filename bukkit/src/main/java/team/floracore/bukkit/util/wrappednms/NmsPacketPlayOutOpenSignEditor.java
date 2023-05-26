package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayOutOpenSignEditor",
                                  maxVer = 17), @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor",
                                                             minVer = 17)})
public interface NmsPacketPlayOutOpenSignEditor extends NmsPacket {
    static NmsPacketPlayOutOpenSignEditor newInstance(NmsBlockPosition nbp) {
        return WrappedObject.getStatic(NmsPacketPlayOutOpenSignEditor.class).staticNewInstance(nbp);
    }

    @WrappedBukkitConstructor
    NmsPacketPlayOutOpenSignEditor staticNewInstance(NmsBlockPosition nbp);

}
