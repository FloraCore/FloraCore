package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

@CommandDescription("获取或设置氧气值")
@CommandPermission("floracore.command.air")
public class AirCommand extends AbstractFloraCoreCommand {
    public AirCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("air get")
    @CommandPermission("floracore.command.air.get")
    @CommandDescription("获取自己的氧气值（单位：ticks）")
    public void getSelf(@NotNull Player s) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Message.COMMAND_AIR_GET_REMAINING_SELF.send(sender, s.getRemainingAir()); // 返回剩余氧气值
        Message.COMMAND_AIR_GET_MAX_SELF.send(sender, s.getMaximumAir()); // 返回最大氧气值
    }

    @CommandMethod("air get <target>")
    @CommandPermission("floracore.command.air.get.other")
    @CommandDescription("获取目标的氧气值（单位：ticks）")
    public void getOther(@NotNull CommandSender s, @NotNull @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Message.COMMAND_AIR_GET_REMAINING_OTHER.send(sender, target.getName(), target.getRemainingAir()); // 返回剩余氧气值
        Message.COMMAND_AIR_GET_MAX_OTHER.send(sender, target.getName(), target.getMaximumAir()); // 返回最大氧气值
    }

    // TODO 错误异常
    /*@CommandMethod("air setmax <value>")
    @CommandPermission("floracore.command.air.set.max")
    @CommandDescription("设置自己的最大氧气值（单位：ticks）")
    public void setOwnMax(@NotNull Player s, @Argument("value") int value) {
        s.setMaximumAir(value); // 设置最大氧气
        Message.COMMAND_AIR_SET_MAX_SELF.send(getPlugin().getSenderFactory().wrap(s), value); // 告知设置成功
    }

    @CommandMethod("air setmax <target> <value>")
    @CommandPermission("floracore.command.air.set.max.other")
    @CommandDescription("设置目标的最大氧气值（单位：ticks）")
    public void setOtherMax(@NotNull CommandSender s, @NotNull @Argument("target") Player target, @Argument("value") int value, @Nullable @Flag("silent") Boolean silent) {
        target.setMaximumAir(value);
        Message.COMMAND_AIR_SET_MAX_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), value); // 告知设置成功
        if (silent == null || !silent) { // 非静音模式
            Message.COMMAND_AIR_SET_MAX_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName(), value); // 告知被设置
        }
    }*/

    // TODO 错误异常
    /*@CommandMethod("air setremaining <value>")
    @CommandPermission("floracore.command.air.set.remaining")
    @CommandDescription("设置自己的剩余氧气值（单位：ticks）")
    public void setOwnRemaining(@NotNull Player s, @Argument("value") int value) {
        s.setRemainingAir(value); // 设置最大氧气
        Message.COMMAND_AIR_SET_REMAINING_SELF.send(getPlugin().getSenderFactory().wrap(s), value); // 告知设置成功
    }

    @CommandMethod("air setremaining <target> <value>")
    @CommandPermission("floracore.command.air.set.remaining.other")
    @CommandDescription("设置目标的剩余氧气值（单位：ticks）")
    public void setOtherRemaining(@NotNull CommandSender s, @NotNull @Argument("target") Player target, @Argument("value") int value, @Nullable @Flag("silent") Boolean silent) {
        target.setRemainingAir(value);
        Message.COMMAND_AIR_SET_REMAINING_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), value); // 告知设置成功
        if (silent == null || !silent) { // 非静音模式
            Message.COMMAND_AIR_SET_REMAINING_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName(), value); // 告知被设置
        }
    }*/
}
