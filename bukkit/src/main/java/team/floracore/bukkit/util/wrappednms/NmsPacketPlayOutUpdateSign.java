package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayOutUpdateSign", maxVer = 17), @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayOutUpdateSign", minVer = 17)})
public interface NmsPacketPlayOutUpdateSign extends NmsPacket {
    static NmsPacketPlayOutUpdateSign newInstance(NmsWorld world,
                                                  NmsBlockPosition blockPosition,
                                                  NmsIChatBaseComponentArray iChatBaseComponents) {
        return WrappedObject.getStatic(NmsPacketPlayOutUpdateSign.class).staticNewInstance(world, blockPosition, iChatBaseComponents);
    }

    @WrappedBukkitConstructor
    NmsPacketPlayOutUpdateSign staticNewInstance(NmsWorld world,
                                                 NmsBlockPosition blockPosition,
                                                 NmsIChatBaseComponentArray iChatBaseComponents);

}
