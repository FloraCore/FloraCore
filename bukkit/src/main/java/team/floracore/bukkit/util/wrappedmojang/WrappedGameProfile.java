package team.floracore.bukkit.util.wrappedmojang;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass(@VersionName(value = "com.mojang.authlib.GameProfile"))
public interface WrappedGameProfile extends WrappedBukkitObject {
    @WrappedBukkitFieldAccessor({@VersionName("id")})
    String getUniqueId();

    @WrappedBukkitFieldAccessor({@VersionName("name")})
    String getName();

    @WrappedBukkitFieldAccessor(@VersionName("name"))
    void setName(String name);
}
