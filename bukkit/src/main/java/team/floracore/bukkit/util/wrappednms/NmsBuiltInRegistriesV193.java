package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass(@VersionName(minVer = 19.3f, value = "net.minecraft.core.registries.BuiltInRegistries"))
public interface NmsBuiltInRegistriesV193 extends WrappedBukkitObject {
    static NmsRegistryBlocks getEntityTypes() {
        return WrappedObject.getStatic(NmsBuiltInRegistriesV193.class).staticGetEntityTypes();
    }

    static NmsRegistryBlocks getItems() {
        return WrappedObject.getStatic(NmsBuiltInRegistriesV193.class).staticGetItems();
    }

    static NmsIRegistry getEnchants() {
        return WrappedObject.getStatic(NmsBuiltInRegistriesV193.class).staticGetEnchants();
    }

    @WrappedBukkitFieldAccessor(@VersionName("#3"))
    NmsRegistryBlocks staticGetEntityTypes();

    @WrappedBukkitFieldAccessor(@VersionName("#4"))
    NmsRegistryBlocks staticGetItems();

    @WrappedBukkitFieldAccessor(@VersionName("#2"))
    NmsIRegistry staticGetEnchants();
}
