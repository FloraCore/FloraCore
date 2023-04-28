package team.floracore.common.commands.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;

@CommandPermission("floracore.command.feed")
@CommandDescription("喂饱一名玩家")
public class FeedCommand extends AbstractFloraCoreCommand {
    public FeedCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("feed")
    @CommandDescription("喂饱您自己")
    public void self(@NotNull Player s) {
        feed(s);
        Message.COMMAND_FEED_SELF.send(getPlugin().getSenderFactory().wrap(s));
    }

    @CommandMethod("feed <target>")
    @CommandDescription("喂饱目标玩家")
    @CommandPermission("floracore.command.feed.other")
    public void other(
            @NotNull CommandSender s,
            @NotNull @Argument("target") Player target
    ) {
        feed(target);
        Message.COMMAND_FEED_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName());
    }

    private void feed(@NotNull Player player) {
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
    }
}
