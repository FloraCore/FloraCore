package team.floracore.bukkit.util;

import org.bukkit.enchantments.Enchantment;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedClass;
import team.floracore.common.util.wrapper.WrappedFieldAccessor;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.Map;

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
