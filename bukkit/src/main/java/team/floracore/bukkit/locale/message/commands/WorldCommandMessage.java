package team.floracore.bukkit.locale.message.commands;

import net.kyori.adventure.text.Component;
import team.floracore.common.locale.message.AbstractMessage;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface WorldCommandMessage extends AbstractMessage {
    Args2<String, Component> COMMAND_TIME_WORLD_CURRENT = (world, time) -> AbstractMessage.prefixed(translatable()
            // 当前 {0} 的时间是 {1}
            .key("floracore.command.time.world.current")
            .color(AQUA)
            .args(text(world).color(GREEN), time));

    Args2<String, Component> COMMAND_TIME_SET = (world, time) -> AbstractMessage.prefixed(translatable()
            // {0} 的时间被设置为 {1}
            .key("floracore.command.time.set")
            .color(AQUA)
            .args(text(world).color(GREEN), time));

    Args2<String, Component> COMMAND_TIME_ADD = (world, time) -> AbstractMessage.prefixed(translatable()
            // {0} 的时间已被 {1} 快进
            .key("floracore.command.time.add")
            .color(AQUA)
            .args(text(world).color(
                            GREEN),
                    time));

    Args1<String> COMMAND_WEATHER_NOSUCH = (weather) -> AbstractMessage.prefixed(translatable()
            // {0} 不是合法的天气类型
            .key("floracore.command.weather.nosuch")
            .color(RED)
            .args(text(weather).color(GREEN))
            .append(FULL_STOP));

    Args2<String, Component> COMMAND_WEATHER_NORMAL = (world, weather) -> AbstractMessage.prefixed(translatable()
            // 你将 {0} 的天气设为 {1}
            .key("floracore.command.weather.normal")
            .color(AQUA)
            .args(text(
                            world).color(
                            GREEN),
                    weather.color(
                            GREEN))
            .append(FULL_STOP));

    Args3<String, Component, String> COMMAND_WEATHER_TIME = (world, weather, time) -> AbstractMessage.prefixed(
            translatable()
                    // 你将 {0} 的天气设为 {1},持续 {2} 秒
                    .key("floracore.command.weather.time")
                    .color(AQUA)
                    .args(text(world).color(GREEN), weather.color(GREEN), text(time).color(GREEN))
                    .append(FULL_STOP));

}
