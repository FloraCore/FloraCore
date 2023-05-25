package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass(@VersionName("obc.block.CraftSign"))
public interface ObcSign extends WrappedBukkitObject {
    @WrappedBukkitFieldAccessor(@VersionName(minVer = 8, maxVer = 12, value = "sign"))
    NmsTileEntitySign getTileEntitySign();
}
