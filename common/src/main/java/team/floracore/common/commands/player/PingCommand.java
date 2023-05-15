package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.util.*;

import java.lang.reflect.*;

import static team.floracore.common.util.ReflectionWrapper.*;

/**
 * Ping命令
 */
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
    public void other(@NotNull CommandSender s, @NotNull @Argument("target") Player target) {
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
            Object entityPlayer = invokeMethod(getHandle, player);
            // return entityPlayer.ping
            //noinspection DataFlowIssue
            return ReflectionWrapper.getFieldValue(getField(entityPlayer.getClass(), "ping"), entityPlayer);
        }
    }
}
