package team.floracore.bukkit.commands.world;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.google.common.collect.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;

import java.util.*;

/**
 * Weather命令
 */
@CommandPermission("floracore.command.weather")
@CommandDescription("设置世界的天气")
public class WeatherBukkitCommand extends FloraCoreBukkitCommand {
    public WeatherBukkitCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("weather <weather> [time]")
    @CommandDescription("设置天气状态并指定持续时间")
    public void weather(final @NotNull Player p,
                        final @NotNull @Argument(value = "weather", suggestions = "weather") String weather,
                        final @Argument(value = "time", suggestions = "commonDurations") String time,
                        @Flag("world") World world) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (world == null) {
            world = p.getWorld();
        }
        final boolean isStorm;
        Component component;
        if (weather.endsWith("sun")) {
            isStorm = false;
            component = MiscMessage.COMMAND_MISC_WEATHER_SUM.build();
        } else if (weather.endsWith("storm") || weather.endsWith("rain")) {
            isStorm = true;
            component = MiscMessage.COMMAND_MISC_WEATHER_STORM.build();
        } else {
            WorldCommandMessage.COMMAND_WEATHER_NOSUCH.send(sender, weather);
            return;
        }
        world.setStorm(isStorm);
        if (time != null) {
            world.setWeatherDuration(Integer.parseInt(time) * 20);
            WorldCommandMessage.COMMAND_WEATHER_TIME.send(sender, world.getName(), component, time);
        } else {
            WorldCommandMessage.COMMAND_WEATHER_NORMAL.send(sender, world.getName(), component);
        }
    }

    @Suggestions("weather")
    public @NotNull List<String> getWeather(final @NotNull CommandContext<CommandSender> sender,
                                            final @NotNull String input) {
        return ImmutableList.of("storm", "sun");
    }
}
