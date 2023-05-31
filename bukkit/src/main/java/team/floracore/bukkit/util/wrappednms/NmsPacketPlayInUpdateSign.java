package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayInUpdateSign",
                                  maxVer = 17), @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayInUpdateSign",
                                                             minVer = 17)})

public interface NmsPacketPlayInUpdateSign extends NmsPacket {
    @WrappedBukkitFieldAccessor(@VersionName("@0"))
    NmsBlockPosition getBlockPosition();

    @WrappedBukkitFieldAccessor(@VersionName("@0"))
    NmsIChatBaseComponentArray getIChatBaseComponents();
}
