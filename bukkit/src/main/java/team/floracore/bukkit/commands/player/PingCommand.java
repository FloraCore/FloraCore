package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

/**
 * Ping命令
 */
@CommandDescription("获取玩家ping延迟")
@CommandPermission("floracore.command.ping")
public class PingCommand extends FloraCoreBukkitCommand {
    public PingCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("ping")
    @CommandDescription("获取您自己的ping延迟")
    public void self(@NotNull Player s) {
        PlayerCommandMessage.COMMAND_PING_SELF.send(getPlugin().getSenderFactory().wrap(s), getPing(s));
    }

    @CommandMethod("ping <target>")
    @CommandDescription("获取一名玩家的ping延迟")
    @CommandPermission("floracore.command.ping.other")
    public void other(@NotNull CommandSender s, @NotNull @Argument("target") Player target) {
        PlayerCommandMessage.COMMAND_PING_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName(), getPing(target));
    }

    public int getPing(@NotNull Player player) {
        if (BukkitWrapper.v17) {
            ObcPlayer op = WrappedObject.wrap(ObcPlayer.class, player);
            return op.getPing();
        }
        NmsEntityPlayer nep = WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityPlayer.class);
        return nep.getPing();
    }
}