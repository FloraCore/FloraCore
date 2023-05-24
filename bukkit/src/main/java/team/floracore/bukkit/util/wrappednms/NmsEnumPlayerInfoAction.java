package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass(@VersionName(maxVer = 17, value = "nms.PacketPlayOutPlayerInfo$EnumPlayerInfoAction"))
public interface NmsEnumPlayerInfoAction extends WrappedBukkitObject {
    @WrappedBukkitFieldAccessor(@VersionName("#0"))
    NmsEnumPlayerInfoAction ADD_PLAYER();

    @WrappedBukkitFieldAccessor(@VersionName("#3"))
    NmsEnumPlayerInfoAction UPDATE_DISPLAY_NAME();

    @WrappedBukkitFieldAccessor(@VersionName("#4"))
    NmsEnumPlayerInfoAction REMOVE_PLAYER();
}
