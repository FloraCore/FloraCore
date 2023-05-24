package team.floracore.bukkit.util.wrappedmojang;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass(@VersionName(value = "com.mojang.authlib.GameProfile"))
public interface WrappedGameProfile extends WrappedBukkitObject {
    @WrappedBukkitFieldAccessor({@VersionName("name")})
    String getName();

    @WrappedBukkitFieldAccessor(@VersionName("name"))
    void setName(String name);
}
