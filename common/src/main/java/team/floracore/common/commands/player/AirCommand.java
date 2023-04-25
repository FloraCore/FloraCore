package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

@CommandDescription("获取或设置氧气值（单位：ticks）")
public class AirCommand extends AbstractFloraCoreCommand {
    public AirCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("air get [target]")
    @CommandPermission("floracore.command.air.get")
    @CommandDescription("获取一名玩家的氧气值（单位：ticks）")
    public void get(@NotNull CommandSender s, @Nullable @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 目标为空，则目标为自己
            // 目标为自己时，发送者必须是玩家
            if (!(s instanceof Player)) { // 不是玩家
                SenderUtil.sendMustBePlayer(sender); // 告知不予执行
                return;
            }
            Player player = (Player) s;
            Message.COMMAND_AIR_GET_SELF_REMAINING.send(sender, player.getRemainingAir()); // 返回剩余氧气值
            Message.COMMAND_AIR_GET_SELF_MAX.send(sender, player.getMaximumAir()); // 返回最大氧气值
        } else { // 指定了目标玩家
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.air.get.other")) { // 玩家没有特定权限
                return;
            }
            Message.COMMAND_AIR_GET_OTHER_REMAINING.send(sender, target.getName(), target.getRemainingAir()); // 返回剩余氧气值
            Message.COMMAND_AIR_GET_OTHER_MAX.send(sender, target.getName(), target.getMaximumAir()); // 返回最大氧气值
        }
    }

    @CommandMethod("air setmax <value> [target]")
    @CommandPermission("floracore.command.air.set.max")
    @CommandDescription("设置一名玩家的最大氧气值（单位：ticks）")
    public void setMax(@NotNull CommandSender s, @Argument("value") int value, @Nullable @Argument("target") Player target, @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 目标为空，则目标为自己
            // 目标为自己时，发送者必须是玩家
            if (!(s instanceof Player)) { // 不是玩家
                SenderUtil.sendMustBePlayer(sender); // 告知不予执行
                return;
            }
            Player player = (Player) s;
            player.setMaximumAir(value); // 设置最大氧气
            Message.COMMAND_AIR_SET_SELF_MAX.send(sender, value); // 告知设置成功
        } else { // 指定了目标玩家
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.air.set.max.other")) { // 发送者没有特定权限
                return;
            }
            target.setMaximumAir(value);
            Message.COMMAND_AIR_SET_OTHER_MAX.send(sender, target.getName(), value); // 告知设置成功
            if (silent == null || !silent) { // 非静音模式
                Message.COMMAND_AIR_SET_FROM_MAX.send(getPlugin().getSenderFactory().wrap(target), s.getName(), value); // 告知被设置
            }
        }
    }

    @CommandMethod("air setremaining <value> [target]")
    @CommandPermission("floracore.command.air.set.remaining")
    @CommandDescription("设置一名玩家的剩余氧气值（单位：ticks）")
    public void setRemaining(@NotNull CommandSender s, @Argument("value") int value, @Nullable @Argument("target") Player target, @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 目标为空，则目标为自己
            // 目标为自己时，发送者必须是玩家
            if (!(s instanceof Player)) { // 不是玩家
                SenderUtil.sendMustBePlayer(sender); // 告知不予执行
                return;
            }
            Player player = (Player) s;
            player.setRemainingAir(value); // 设置剩余氧气
            Message.COMMAND_AIR_SET_SELF_REMAINING.send(sender, value); // 告知设置成功
        } else { // 指定了目标玩家
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.air.set.remaining.other")) { // 发送者没有特定权限
                return;
            }
            target.setRemainingAir(value);
            Message.COMMAND_AIR_SET_OTHER_REMAINING.send(sender, target.getName(), value); // 告知设置成功
            if (silent == null || !silent) { // 非静音模式
                Message.COMMAND_AIR_SET_FROM_REMAINING.send(getPlugin().getSenderFactory().wrap(target), s.getName(), value); // 告知被设置
            }
        }
    }
}
