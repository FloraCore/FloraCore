package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.PacketListener", maxVer = 17), @VersionName(value = "net.minecraft.network.PacketListener", minVer = 17)})
public interface NmsPacketListener extends WrappedBukkitObject {
}
