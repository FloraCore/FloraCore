package team.floracore.bukkit.util;

import org.bukkit.enchantments.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

@WrappedClass("org.bukkit.enchantments.Enchantment")
public interface WrappedEnchantment extends WrappedBukkitObject {
    static Map<String, Enchantment> getByName() {
        return WrappedObject.getStatic(WrappedEnchantment.class).staticGetByName();
    }

    static Map<Integer, Enchantment> getByIdV_13() {
        return WrappedObject.getStatic(WrappedEnchantment.class).staticGetByIdV_13();
    }

    @WrappedFieldAccessor("byName")
    Map<String, Enchantment> staticGetByName();

    @WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "byId"))
    Map<Integer, Enchantment> staticGetByIdV_13();
}
