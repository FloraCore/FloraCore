package team.floracore.plugin.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.plugin.*;
import team.floracore.plugin.command.*;

/**
 * MaxHealth命令
 */
@CommandDescription("获取和设置最大生命值")
@CommandPermission("floracore.command.maxhealth")
public class MaxHealthCommand extends AbstractFloraCoreCommand {
    public MaxHealthCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("maxhealth|maxhp get")
    @CommandDescription("获取自己的最大生命值")
    @CommandPermission("floracore.command.maxhealth.get")
    public void getOwnMaxHealth(@NotNull Player s) {
        Message.COMMAND_MAXHEALTH_GET_SELF.send(getPlugin().getSenderFactory().wrap(s), MultipleVersionsUtil.getMaxHealth(s));
    }

    @CommandMethod("maxhealth|maxhp get <target>")
    @CommandDescription("获取目标的最大生命值")
    @CommandPermission("floracore.command.maxhealth.get.other")
    public void getOtherMaxHealth(@NotNull CommandSender s, @NotNull @Argument("target") Player target) {
        Message.COMMAND_MAXHEALTH_GET_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), MultipleVersionsUtil.getMaxHealth(target));
    }

    @CommandMethod("maxhealth|maxhp set <value> [target]")
    @CommandDescription("设置目标的最大生命值")
    @CommandPermission("floracore.command.maxhealth.set.other")
    public void setOtherMaxHealth(
            @NotNull CommandSender s,
            @Argument("value") Double value,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) {
            if (s instanceof Player) {
                Player player = (Player) s;
                MultipleVersionsUtil.setMaxHealth(player, value);
                Message.COMMAND_MAXHEALTH_SET_SELF.send(sender, value);
            } else {
                SenderUtil.sendMustBePlayer(sender, s.getClass());
            }
        } else {
            MultipleVersionsUtil.setMaxHealth(target, value);
            Message.COMMAND_MAXHEALTH_SET_OTHER.send(sender, target.getName(), value);
            if (silent == null || !silent) {
                Message.COMMAND_MAXHEALTH_SET_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName(), value);
            }
        }
    }
}
