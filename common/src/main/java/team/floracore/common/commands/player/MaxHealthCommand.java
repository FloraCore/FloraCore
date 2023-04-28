package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.MultipleVersionsUtil;

@CommandDescription("获取和设置最大生命值")
@CommandPermission("floracore.command.maxhealth")
public class MaxHealthCommand extends AbstractFloraCoreCommand {
    public MaxHealthCommand(FloraCorePlugin plugin) {
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
    public void getOtherMaxHealth(
            @NotNull CommandSender s,
            @NotNull @Argument("target") Player target
    ) {
        Message.COMMAND_MAXHEALTH_GET_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), MultipleVersionsUtil.getMaxHealth(target));
    }

    @CommandMethod("maxhealth|maxhp set <value>")
    @CommandDescription("设置自己的最大生命值")
    @CommandPermission("floracore.command.maxhealth.set")
    public void setOwnMaxHealth(
            @NotNull Player s,
            @Argument("value") double value
    ) {
        MultipleVersionsUtil.setMaxHealth(s, value);
        Message.COMMAND_MAXHEALTH_SET_SELF.send(getPlugin().getSenderFactory().wrap(s), value);
    }

    @CommandMethod("maxhealth|maxhp set <target> <value>")
    @CommandDescription("设置目标的最大生命值")
    @CommandPermission("floracore.command.maxhealth.set.other")
    public void setOtherMaxHealth(
            @NotNull Player s,
            @NotNull @Argument("target") Player target,
            @Argument("value") double value,
            @Nullable @Flag("silent") Boolean silent
    ) {
        MultipleVersionsUtil.setMaxHealth(target, value);
        Message.COMMAND_MAXHEALTH_SET_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), value);
        if (silent == null || !silent) {
            Message.COMMAND_MAXHEALTH_SET_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName(), value);
        }
    }
}
