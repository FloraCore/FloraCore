package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.Packet",
                                  maxVer = 17), @VersionName(value = "net.minecraft.network.protocol.Packet",
                                                             minVer = 17)})
public interface NmsPacket extends WrappedBukkitObject {
}
