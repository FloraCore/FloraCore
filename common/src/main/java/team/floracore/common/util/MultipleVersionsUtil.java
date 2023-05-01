package team.floracore.common.util;

import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.*;

import java.lang.reflect.Method;

/**
 * 一些跨版本的常用类
 */
public final class MultipleVersionsUtil {
    private MultipleVersionsUtil() {
    }


    /**
     * 获取玩家最大生命值
     * 低版本没有Attribute概念，直接调用getMaxHealth
     *
     * @param player 玩家
     * @return 玩家最大生命值
     */
    public static double getMaxHealth(@NotNull Player player) {
        try {
            Class<?> classAttribute = Class.forName("org.bukkit.attribute.Attribute");
            // Attribute enumGenericMaxHealth = Attribute.GENERIC_MAX_HEALTH
            Object enumGenericMaxHealth = ReflectionWrapper.getStaticFieldValue(ReflectionWrapper.getField(classAttribute, "GENERIC_MAX_HEALTH"));
            // AttributeInstance attrInstance = player.getAttribute(enumGenericMaxHealth);
            Object attrInstance = ReflectionWrapper.invokeMethod(
                    ReflectionWrapper.getMethod(player.getClass(), "getAttribute", classAttribute),
                    player, enumGenericMaxHealth
            );
            // return attrInstance.getValue()
            return ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(attrInstance.getClass(), "getValue"), attrInstance);
        } catch (ClassNotFoundException e) { // 没有org.bukkit.attribute.Attribute这个类，说明不存在Attribute概念，应该是低版本
            // return player.getMaxHealth()
            return ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(Damageable.class, "getMaxHealth"), player);
        }
    }

    /**
     * 设置玩家最大生命值
     * 低版本没有Attribute概念，直接调用setMaxHealth
     *
     * @param player 玩家
     * @param value  数值
     */
    public static void setMaxHealth(@NotNull Player player, double value) {
        try {
            Class<?> classAttribute = Class.forName("org.bukkit.attribute.Attribute");
            // Attribute enumGenericMaxHealth = Attribute.GENERIC_MAX_HEALTH
            Object enumGenericMaxHealth = ReflectionWrapper.getStaticFieldValue(ReflectionWrapper.getField(classAttribute, "GENERIC_MAX_HEALTH"));
//            AttributeInstance attrInstance = player.getAttribute(enumGenericMaxHealth);
            Object attrInstance = ReflectionWrapper.invokeMethod(
                    ReflectionWrapper.getMethod(player.getClass(), "getAttribute", classAttribute),
                    player, enumGenericMaxHealth
            );
//            attrInstance.setBaseValue(value);
            ReflectionWrapper.invokeMethod(
                    ReflectionWrapper.getMethod(attrInstance.getClass(), "setBaseValue", double.class),
                    attrInstance, value
            );
        } catch (ClassNotFoundException e) { // 没有org.bukkit.attribute.Attribute这个类，说明不存在Attribute概念，应该是低版本
            // return player.setMaxHealth(value)
            ReflectionWrapper.invokeMethod(
                    ReflectionWrapper.getMethod(Damageable.class, "setMaxHealth", double.class),
                    player
            );
        }
    }

    /**
     * 获取玩家主手上的物品
     * 低版本没有主副手之分，所以可以直接调用
     * 高版本有主副手之分，需要指定主手和副手
     *
     * @param inventory 玩家物品栏
     * @return 玩家主手上的物品
     */
    public static @Nullable ItemStack getItemInMainHand(@NotNull PlayerInventory inventory) {
        try {
            Method method = PlayerInventory.class.getMethod("getItemInMainHand");
            return ReflectionWrapper.invokeMethod(method, inventory);
        } catch (NoSuchMethodException e) {
            return ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(PlayerInventory.class, "getItemInHand"), inventory);
        }
    }
}
