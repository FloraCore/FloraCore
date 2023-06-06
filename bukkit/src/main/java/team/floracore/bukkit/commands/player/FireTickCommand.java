package team.floracore.bukkit.commands.player;

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
 * FireTick命令
 */
@CommandDescription("floracore.command.description.firetick")
@CommandPermission("floracore.command.firetick")
public class FireTickCommand extends FloraCoreBukkitCommand {
    public FireTickCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("firetick|burn <time> [target]")
    @CommandDescription("floracore.command.description.firetick")
    public void firetick(
            @NotNull CommandSender s,
            @Argument("time") int time,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 指定自己
            if (s instanceof Player) {
                ((Player) s).setFireTicks(time * 20);
                PlayerCommandMessage.COMMAND_FIRETICK_SELF.send(sender, time);
            } else {
                SenderUtil.sendMustBe(sender, s.getClass(), Player.class);
            }
        } else {
            target.setFireTicks(time * 20);
            PlayerCommandMessage.COMMAND_FIRETICK_OTHER.send(sender, target.getName(), time);
            if (silent == null || !silent) {
                PlayerCommandMessage.COMMAND_FIRETICK_FROM.send(getPlugin().getSenderFactory().wrap(target),
                        sender.getName(),
                        time);
            }
        }
    }
}
