package team.floracore.common.commands.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

public class FlyCommand extends AbstractFloraCoreCommand {
    public FlyCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("fly")
    @CommandPermission("floracore.command.fly")
    public void self(@NotNull Player s) {
        boolean old = s.getAllowFlight();
        s.setAllowFlight(!old);
        // TODO 设置自动同步飞行状态
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (old) {
            Message.COMMAND_FLY_DISABLE_SELF.send(sender);
        } else {
            Message.COMMAND_FLY_ENABLE_SELF.send(sender);
        }
    }

    @CommandMethod("fly <target> [silent]")
    @CommandPermission("floracore.command.fly.other")
    public void other(@NotNull Player s, @Argument("target") Player target, @Argument("silent") @Nullable Boolean silent) {
        boolean old = target.getAllowFlight();
        target.setAllowFlight(!old);
        // TODO 设置自动同步飞行状态
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Sender targetSender = getPlugin().getSenderFactory().wrap(target);
        if (old) {
            Message.COMMAND_FLY_DISABLE_OTHER.send(sender, target.getName());
            if (silent == null || !silent) {
                Message.COMMAND_FLY_DISABLE_FROM.send(targetSender, s.getName());
            }
        } else {
            Message.COMMAND_FLY_ENABLE_OTHER.send(sender, target.getName());
            if (silent == null || !silent) {
                Message.COMMAND_FLY_ENABLE_FROM.send(targetSender, s.getName());
            }
        }
    }
}
