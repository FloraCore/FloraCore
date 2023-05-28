package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

/**
 * FireTick命令
 */
@CommandPermission("floracore.command.firetick")
@CommandDescription("floracore.command.description.firetick")
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
