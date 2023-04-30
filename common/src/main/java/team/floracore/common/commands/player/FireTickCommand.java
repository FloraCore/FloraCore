package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

/**
 * FireTick命令
 */
@CommandDescription("设置玩家着火时间")
@CommandPermission("floracore.command.firetick")
public class FireTickCommand extends AbstractFloraCoreCommand {
    public FireTickCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    // TODO 错误异常
    /*@CommandMethod("firetick|burn <time>")
    @CommandDescription("设置自己的着火时间（单位：秒）")
    public void self(@NotNull Player s, @Argument("time") int time) {
        s.setFireTicks(time * 20);
        Message.COMMAND_FIRETICK_SELF.send(getPlugin().getSenderFactory().wrap(s), time);
    }

    @CommandMethod("firetick|burn <target> <time>")
    @CommandDescription("设置目标的着火时间（单位：秒）")
    @CommandPermission("floracore.command.firetick.other")
    public void other(@NotNull CommandSender s, @NotNull @Argument("target") Player target, @Argument("time") int time, @Nullable @Flag("silent") Boolean silent) {
        target.setFireTicks(time * 20);
        Message.COMMAND_FIRETICK_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), time);
        if (silent == null || !silent) {
            Message.COMMAND_FIRETICK_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName(), time);
        }
    }*/
}
