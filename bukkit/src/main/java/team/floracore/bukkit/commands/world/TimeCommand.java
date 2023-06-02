package team.floracore.bukkit.commands.world;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.WorldCommandMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.util.DescParseTickFormat;
import team.floracore.common.util.NumberUtil;

import java.util.*;

/**
 * Time命令
 */
@CommandPermission("floracore.command.time")
@CommandDescription("世界时间设置和显示")
public class TimeCommand extends FloraCoreBukkitCommand {
    public TimeCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("time")
    @CommandDescription("显示所有世界的时间")
    public void time(final @NotNull CommandSender s) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        final Set<World> worlds;

        worlds = getWorlds(s, null);
        assert worlds != null;
        getWorldsTime(sender, worlds);
    }

    /**
     * Parses worlds from command args, otherwise returns all worlds.
     */
    private Set<World> getWorlds(final CommandSender sender, final String selector) {
        Server server = Bukkit.getServer();
        final Set<World> worlds = new HashSet<>();

        // If there is no selector we want the world the user is currently in. Or all worlds if it isn't a user.
        if (selector == null) {
            if (sender instanceof Player) {
                worlds.add(((Player) sender).getWorld());
            } else {
                worlds.addAll(server.getWorlds());
            }
            return worlds;
        }

        // Try to find the world with name = selector
        final World world = server.getWorld(selector);
        if (world != null) {
            worlds.add(world);
        } else if (selector.equalsIgnoreCase("*")) { // If that fails, Is the argument something like "*" or "all"?
            worlds.addAll(server.getWorlds());
        } else {
            Sender s = getPlugin().getSenderFactory().wrap(sender);
            MiscMessage.COMMAND_MISC_WORLD_INVALID.send(s);
            return null;
        }
        return worlds;
    }

    private void getWorldsTime(final Sender sender, final Collection<World> worlds) {
        if (worlds.size() == 1) {
            final Iterator<World> iter = worlds.iterator();
            MiscMessage.DURATION_FORMAT.send(sender, iter.next().getTime());
            return;
        }

        for (final World world : worlds) {
            WorldCommandMessage.COMMAND_TIME_WORLD_CURRENT.send(sender,
                    world.getName(),
                    MiscMessage.DURATION_FORMAT.build(world.getTime()));
        }
    }

    @CommandMethod("time set <time>")
    @CommandPermission("floracore.command.time.set")
    @CommandDescription("设置当前（或指定）世界的时间")
    public void setTime(final @NotNull CommandSender s,
                        final @NotNull @Argument(value = "time", suggestions = "timeNames") String time,
                        final @Nullable @Flag(value = "world", suggestions = "worlds-all") String world) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        final long timeTick;
        final Set<World> worlds;

        try {
            timeTick = DescParseTickFormat.parse(NumberUtil.isInt(time) ? (time + "t") : time);
            worlds = getWorlds(s, world);
        } catch (final NumberFormatException e) {
            MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
            return;
        }

        final StringJoiner joiner = new StringJoiner(", ");
        if (worlds != null) {
            for (final World w : worlds) {
                long t = w.getTime();
                t -= t % 24000;
                w.setTime(t + (24000) + timeTick);
                joiner.add(w.getName());
            }
            WorldCommandMessage.COMMAND_TIME_SET.send(sender,
                    joiner.toString(),
                    MiscMessage.DURATION_FORMAT.build(timeTick));
        }
    }

    @CommandMethod("time add <time>")
    @CommandPermission("floracore.command.time.add")
    @CommandDescription("快进当前（或指定）世界的时间")
    public void addTime(final @NotNull CommandSender s,
                        final @NotNull @Argument(value = "time", suggestions = "timeNumbers") String time,
                        final @Nullable @Flag(value = "world", suggestions = "worlds-all") String world) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        final long timeTick;
        final Set<World> worlds;
        try {
            timeTick = DescParseTickFormat.parse(NumberUtil.isInt(time) ? (time + "t") : time);
            worlds = getWorlds(s, world);
        } catch (final NumberFormatException e) {
            MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
            return;
        }

        final StringJoiner joiner = new StringJoiner(", ");
        if (worlds != null) {
            for (final World w : worlds) {
                long t = w.getTime();
                w.setTime(t + timeTick);
                joiner.add(w.getName());
            }
            WorldCommandMessage.COMMAND_TIME_ADD.send(sender,
                    joiner.toString(),
                    MiscMessage.DURATION_FORMAT.build(timeTick));
        }
    }

    @Suggestions("timeNames")
    public @NotNull List<String> getTimeNames(final @NotNull CommandContext<CommandSender> sender,
                                              final @NotNull String input) {
        return ImmutableList.of("sunrise", "day", "morning", "noon", "afternoon", "sunset", "night", "midnight");
    }

    @Suggestions("timeNumbers")
    public @NotNull List<String> getTimeNumbers(final @NotNull CommandContext<CommandSender> sender,
                                                final @NotNull String input) {
        return ImmutableList.of("1000", "2000", "3000", "4000", "5000");
    }
}
