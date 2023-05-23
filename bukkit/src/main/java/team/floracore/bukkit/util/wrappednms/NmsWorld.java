package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.World", maxVer = 17), @VersionName(value = "net.minecraft.world.level.World", minVer = 17)})
public interface NmsWorld extends WrappedBukkitObject {
}
