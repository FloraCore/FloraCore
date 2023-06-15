package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.util.SenderUtil;

/**
 * Air命令
 */
@CommandDescription("floracore.command.description.air")
@CommandPermission("floracore.command.air")
public class AirCommand extends FloraCoreBukkitCommand {
    public AirCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("air get")
    @CommandDescription("floracore.command.description.air.get")
    @CommandPermission("floracore.command.air.get")
    public void getSelf(@NotNull Player s) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        PlayerCommandMessage.COMMAND_AIR_GET_REMAINING_SELF.send(sender, s.getRemainingAir()); // 返回剩余氧气值
        PlayerCommandMessage.COMMAND_AIR_GET_MAX_SELF.send(sender, s.getMaximumAir()); // 返回最大氧气值
    }

    @CommandMethod("air get <target>")
    @CommandDescription("floracore.command.description.air.get.other")
    @CommandPermission("floracore.command.air.get.other")
    public void getOther(@NotNull CommandSender s, @NotNull @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        PlayerCommandMessage.COMMAND_AIR_GET_REMAINING_OTHER.send(sender,
                target.getName(),
                target.getRemainingAir()); // 返回剩余氧气值
        PlayerCommandMessage.COMMAND_AIR_GET_MAX_OTHER.send(sender,
                target.getName(),
                target.getMaximumAir()); // 返回最大氧气值
    }

    @CommandMethod("air setmax <value> [target]")
    @CommandDescription("floracore.command.description.air.set.max")
    @CommandPermission("floracore.command.air.set.max")
    public void setMax(
            @NotNull CommandSender s,
            @Argument("value") int value,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 指定自己
            if (s instanceof Player) {
                Player player = (Player) s;
                player.setMaximumAir(value); // 设置最大氧气
                PlayerCommandMessage.COMMAND_AIR_SET_MAX_SELF.send(sender, value); // 告知设置成功
                return;
            }
            SenderUtil.sendMustBe(sender, s.getClass(), Player.class);
        } else { // 指定target
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.air.set.max.other")) {
                return;
            }
            target.setMaximumAir(value);
            PlayerCommandMessage.COMMAND_AIR_SET_MAX_OTHER.send(sender, target.getName(), value); // 告知设置成功
            if (silent == null || !silent) { // 非静音模式
                PlayerCommandMessage.COMMAND_AIR_SET_MAX_FROM.send(getPlugin().getSenderFactory().wrap(target),
                        s.getName(),
                        value); // 告知被设置
            }
        }
    }

    @CommandMethod("air setremaining <value> [target]")
    @CommandPermission("floracore.command.air.set.remaining")
    @CommandDescription("floracore.command.description.air.set.remaining")
    public void setRemaining(
            @NotNull CommandSender s,
            @Argument("value") int value,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 指定自己
            if (s instanceof Player) {
                Player player = (Player) s;
                player.setRemainingAir(value); // 设置剩余氧气
                PlayerCommandMessage.COMMAND_AIR_SET_REMAINING_SELF.send(sender, value); // 告知设置成功
                return;
            }
            SenderUtil.sendMustBe(sender, s.getClass(), Player.class);
        } else { // 指定target
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.air.set.remaining.other")) {
                return;
            }
            target.setRemainingAir(value);
            PlayerCommandMessage.COMMAND_AIR_SET_REMAINING_OTHER.send(sender, target.getName(), value); // 告知设置成功
            if (silent == null || !silent) { // 非静音模式
                PlayerCommandMessage.COMMAND_AIR_SET_REMAINING_FROM.send(getPlugin().getSenderFactory().wrap(target),
                        s.getName(),
                        value); // 告知被设置
            }
        }
    }
}
