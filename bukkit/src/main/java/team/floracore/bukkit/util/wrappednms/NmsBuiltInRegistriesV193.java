package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass(@VersionName(minVer = 19.3f, value = "net.minecraft.core.registries.BuiltInRegistries"))
public interface NmsBuiltInRegistriesV193 extends WrappedBukkitObject {
    static NmsRegistryBlocks getEntityTypes() {
        return WrappedObject.getStatic(NmsBuiltInRegistriesV193.class).staticGetEntityTypes();
    }

    @WrappedBukkitFieldAccessor(@VersionName("#3"))
    NmsRegistryBlocks staticGetEntityTypes();

    static NmsRegistryBlocks getItems() {
        return WrappedObject.getStatic(NmsBuiltInRegistriesV193.class).staticGetItems();
    }

    @WrappedBukkitFieldAccessor(@VersionName("#4"))
    NmsRegistryBlocks staticGetItems();

    static NmsIRegistry getEnchants() {
        return WrappedObject.getStatic(NmsBuiltInRegistriesV193.class).staticGetEnchants();
    }

    @WrappedBukkitFieldAccessor(@VersionName("#2"))
    NmsIRegistry staticGetEnchants();
}
