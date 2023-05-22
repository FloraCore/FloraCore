package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;

/**
 * Feed命令
 */
@CommandPermission("floracore.command.feed")
@CommandDescription("喂饱一名玩家")
public class FeedBukkitCommand extends FloraCoreBukkitCommand {
    public FeedBukkitCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("feed")
    @CommandDescription("喂饱您自己")
    public void self(@NotNull Player s) {
        feed(s);
        PlayerCommandMessage.COMMAND_FEED_SELF.send(getPlugin().getSenderFactory().wrap(s));
    }

    @CommandMethod("feed <target>")
    @CommandDescription("喂饱目标玩家")
    @CommandPermission("floracore.command.feed.other")
    public void other(@NotNull CommandSender s, @NotNull @Argument("target") Player target, @Nullable @Flag("silent") Boolean silent) {
        feed(target);
        PlayerCommandMessage.COMMAND_FEED_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName());
        if (silent == null || !silent) {
            PlayerCommandMessage.COMMAND_FEED_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName());
        }
    }

    private void feed(@NotNull Player player) {
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
    }
}
