package team.floracore.bukkit.util.wrappedmojang;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitConstructor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.UUID;

@WrappedBukkitClass(@VersionName(value = "com.mojang.authlib.GameProfile"))
public interface WrappedGameProfile extends WrappedBukkitObject {
    static WrappedGameProfile newInstance(UUID uuid, String name) {
        return WrappedObject.getStatic(WrappedGameProfile.class).staticNewInstance(uuid, name);
    }

    @WrappedBukkitConstructor
    WrappedGameProfile staticNewInstance(UUID uuid, String name);

    @WrappedBukkitFieldAccessor({@VersionName("id")})
    UUID getUniqueId();

    @WrappedBukkitFieldAccessor({@VersionName("name")})
    String getName();

    @WrappedBukkitFieldAccessor(@VersionName("name"))
    void setName(String name);

    @WrappedBukkitFieldAccessor({@VersionName("properties")})
    WrappedPropertyMap getProperties();

    @WrappedBukkitFieldAccessor({@VersionName("properties")})
    void setProperties(WrappedPropertyMap properties);

    @WrappedBukkitFieldAccessor({@VersionName("legacy")})
    boolean isLegacy();

    @WrappedBukkitFieldAccessor({@VersionName("legacy")})
    void setLegacy(boolean legacy);
}
