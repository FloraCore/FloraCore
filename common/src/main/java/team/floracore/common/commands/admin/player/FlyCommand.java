package team.floracore.common.commands.admin.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

public class FlyCommand extends AbstractFloraCoreCommand {
    public FlyCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("fly")
    @CommandPermission("floracore.fly")
    public void self(@NotNull Player s) {
        boolean old = s.getAllowFlight();
        s.setAllowFlight(!old);
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (old) {
            Message.COMMAND_FLY_DISABLE_SELF.send(sender);
        } else {
            Message.COMMAND_FLY_ENABLE_SELF.send(sender);
        }
    }

    @CommandMethod("fly <target> [silent]")
    @CommandPermission("floracore.fly.other")
    public void other(@NotNull Player s, @Argument("target") Player target, @Argument(value = "silent", suggestions = "booleans") boolean silent) {
        boolean old = target.getAllowFlight();
        target.setAllowFlight(!old);
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Sender targetSender = getPlugin().getSenderFactory().wrap(target);
        if (silent) { // 静音模式
            return;
        }
        if (old) {
            Message.COMMAND_FLY_DISABLE_OTHER.send(sender, target.getName());
            Message.COMMAND_FLY_DISABLE_FROM.send(targetSender, s.getName());
        } else {
            Message.COMMAND_FLY_ENABLE_OTHER.send(sender, target.getName());
            Message.COMMAND_FLY_ENABLE_FROM.send(targetSender, s.getName());
        }
    }
}
