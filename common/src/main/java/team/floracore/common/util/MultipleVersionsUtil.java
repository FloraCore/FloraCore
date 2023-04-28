package team.floracore.common.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            return ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(Player.class, "getMaxHealth"), player);
        }
    }

    /**
     * 设置玩家最大生命值
     * 低版本没有Attribute概念，直接调用setMaxHealth
     *
     * @param player 玩家
     * @param value 数值
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
                    ReflectionWrapper.getMethod(Player.class, "setMaxHealth", double.class),
                    player
            );
        }
    }
}
