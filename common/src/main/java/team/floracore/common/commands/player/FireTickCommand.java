package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.common.command.*;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.Sender;
import team.floracore.common.util.SenderUtil;

@CommandDescription("设置玩家着火时间")
@CommandPermission("floracore.command.firetick")
public class FireTickCommand extends AbstractFloraCoreCommand {
    public FireTickCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("firetick|burn <time> [target]")
    @CommandDescription("设置自己的着火时间（单位：秒）")
    public void self(
            @NotNull CommandSender s,
            @Argument("time") int time,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 指定自己
            if (s instanceof Player) {
                ((Player) s).setFireTicks(time * 20);
                Message.COMMAND_FIRETICK_SELF.send(sender, time);
            } else {
                SenderUtil.sendMustBePlayer(sender, s.getClass());
            }
        } else {
            target.setFireTicks(time * 20);
            Message.COMMAND_FIRETICK_OTHER.send(sender, target.getName(), time);
            if (silent == null || !silent) {
                Message.COMMAND_FIRETICK_FROM.send(getPlugin().getSenderFactory().wrap(target), sender.getName(), time);
            }
        }
    }
}
