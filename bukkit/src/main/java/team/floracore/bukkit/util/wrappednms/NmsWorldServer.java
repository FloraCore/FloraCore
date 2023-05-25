package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.WorldServer", maxVer = 17), @VersionName(value = {"net.minecraft.world.level.WorldServer", "net.minecraft.server.level.WorldServer"}, minVer = 17)})
public interface NmsWorldServer extends NmsWorld {
}
