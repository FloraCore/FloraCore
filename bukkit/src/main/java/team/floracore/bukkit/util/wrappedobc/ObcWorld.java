package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass(@VersionName("obc.CraftWorld"))
public interface ObcWorld extends WrappedBukkitObject {
    @WrappedMethod("getHandle")
    NmsWorldServer getHandle();
}
