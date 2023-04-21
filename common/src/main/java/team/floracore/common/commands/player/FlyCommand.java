package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

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
    public void other(@NotNull Player s, @Argument("target") Player target, @Argument("silent") @Nullable Boolean silent) {
        boolean old = target.getAllowFlight();
        target.setAllowFlight(!old);
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
