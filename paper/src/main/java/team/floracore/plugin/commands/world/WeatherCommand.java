package team.floracore.plugin.commands.world;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.google.common.collect.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.plugin.*;
import team.floracore.plugin.command.*;

import java.util.*;

/**
 * Weather命令
 */
@CommandPermission("floracore.command.weather")
@CommandDescription("设置世界的天气")
public class WeatherCommand extends AbstractFloraCoreCommand {
    public WeatherCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("weather <weather> [time]")
    @CommandDescription("设置天气状态并指定持续时间")
    public void weather(final @NonNull Player p, final @NonNull @Argument(value = "weather", suggestions = "weather") String weather, final @Argument(value = "time", suggestions = "commonDurations") String time, @Flag("world") World world) {
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
            Message.COMMAND_WEATHER_NOSUCH.send(sender, weather);
            return;
        }
        world.setStorm(isStorm);
        if (time != null) {
            world.setWeatherDuration(Integer.parseInt(time) * 20);
            Message.COMMAND_WEATHER_TIME.send(sender, world, component, time);
        } else {
            Message.COMMAND_WEATHER_NORMAL.send(sender, world, component);
        }
    }

    @Suggestions("weather")
    public @NonNull List<String> getWeather(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        return ImmutableList.of("storm", "sun");
    }
}
