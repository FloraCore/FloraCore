package team.floracore.bukkit.command.impl.world;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.WorldCommandMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;

import java.util.List;

/**
 * Weather命令
 */
@CommandDescription("floracore.command.description.weather")
@CommandPermission("floracore.command.weather")
public class WeatherCommand extends FloraCoreBukkitCommand {
    public WeatherCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("weather <weather> [time]")
    @CommandDescription("floracore.command.description.weather.set")
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
