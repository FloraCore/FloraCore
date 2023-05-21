package team.floracore.plugin.commands.world;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;
import team.floracore.plugin.*;
import team.floracore.plugin.command.*;

import java.util.*;

/**
 * Time命令
 */
@CommandPermission("floracore.command.time")
@CommandDescription("世界时间设置和显示")
public class TimeCommand extends AbstractFloraCoreCommand {
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

    @CommandMethod("time set <time>")
    @CommandPermission("floracore.command.time.set")
    @CommandDescription("设置当前（或指定）世界的时间")
    public void setTime(final @NotNull CommandSender s, final @NotNull @Argument(value = "time", suggestions = "timeNames") String time, final @Nullable @Flag(value = "world", suggestions = "worlds-all") String world) {
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
            Message.COMMAND_TIME_SET.send(sender, joiner.toString(), Message.DURATION_FORMAT.build(timeTick));
        }
    }

    @CommandMethod("time add <time>")
    @CommandPermission("floracore.command.time.add")
    @CommandDescription("快进当前（或指定）世界的时间")
    public void addTime(final @NotNull CommandSender s, final @NotNull @Argument(value = "time", suggestions = "timeNumbers") String time, final @Nullable @Flag(value = "world", suggestions = "worlds-all") String world) {
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
            Message.COMMAND_TIME_ADD.send(sender, joiner.toString(), Message.DURATION_FORMAT.build(timeTick));
        }
    }

    private void getWorldsTime(final Sender sender, final Collection<World> worlds) {
        if (worlds.size() == 1) {
            final Iterator<World> iter = worlds.iterator();
            Message.DURATION_FORMAT.send(sender, iter.next().getTime());
            return;
        }

        for (final World world : worlds) {
            Message.COMMAND_TIME_WORLD_CURRENT.send(sender, world, Message.DURATION_FORMAT.build(world.getTime()));
        }
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

    @Suggestions("timeNames")
    public @NonNull List<String> getTimeNames(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        return ImmutableList.of("sunrise", "day", "morning", "noon", "afternoon", "sunset", "night", "midnight");
    }

    @Suggestions("timeNumbers")
    public @NonNull List<String> getTimeNumbers(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        return ImmutableList.of("1000", "2000", "3000", "4000", "5000");
    }
}
