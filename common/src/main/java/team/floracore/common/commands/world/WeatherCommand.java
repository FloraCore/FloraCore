package team.floracore.common.commands.world;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import java.util.*;

public class WeatherCommand extends AbstractFloraCoreCommand {
    public WeatherCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("weather <weather> [time]")
    @CommandPermission("floracore.command.weather")
    @CommandDescription("设置天气状态并指定持续时间")
    public void weather(final @NonNull Player p, final @NonNull @Argument(value = "weather", suggestions = "weather") String weather, final @Argument(value = "time", suggestions = "commonDurations") Integer time, @Flag("world") World world) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (world == null) {
            world = p.getWorld();
        }
        final boolean isStorm;
        if (weather.endsWith("sun")) {
            isStorm = false;
        } else if (weather.endsWith("storm") || weather.endsWith("rain")) {
            isStorm = true;
        } else {
            Message.COMMAND_WEATHER_NOSUCH.send(sender, weather);
            return;
        }
        world.setStorm(isStorm);
        if (time != null) {
            world.setWeatherDuration(time * 20);
            Message.COMMAND_WEATHER_TIME.send(sender, world, weather, time);
        } else {
            Message.COMMAND_WEATHER_NORMAL.send(sender, world, weather);
        }
    }

    @Suggestions("weather")
    public @NonNull List<String> getWeather(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        return ImmutableList.of("storm", "sun");
    }
}
