package team.floracore.common.commands.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.ReflectionWrapper;

import java.lang.reflect.Method;

@CommandDescription("获取玩家ping延迟")
@CommandPermission("floracore.command.ping")
public class PingCommand extends AbstractFloraCoreCommand {
    public PingCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("ping")
    @CommandDescription("获取您自己的ping延迟")
    public void self(@NotNull Player s) {
        Message.COMMAND_PING_SELF.send(getPlugin().getSenderFactory().wrap(s), getPing(s));
    }

    @CommandMethod("ping <target>")
    @CommandDescription("获取一名玩家的ping延迟")
    @CommandPermission("floracore.command.ping.other")
    public void other(
            @NotNull CommandSender s,
            @NotNull @Argument("target") Player target
    ) {
        Message.COMMAND_PING_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), getPing(target));
    }

    public int getPing(@NotNull Player player) {
        try {
            // 高版本可以直接getPing()
            Method methodGetPing = player.getClass().getMethod("getPing");
            return ReflectionWrapper.invokeMethod(methodGetPing, player);
        } catch (NoSuchMethodException e) {
            // 低版本需要调用NMS
            // EntityPlayer entityPlayer = player.getHandle()
            Object entityPlayer = ReflectionWrapper.invokeMethod(ReflectionWrapper.getHandle, player);
            // return entityPlayer.ping
            //noinspection DataFlowIssue
            return ReflectionWrapper.getFieldValue(ReflectionWrapper.getField(entityPlayer.getClass(), "ping"), entityPlayer);
        }
    }
}
