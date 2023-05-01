package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

/**
 * RealName命令
 */
@CommandPermission("floracore.command.realname")
public class RealNameCommand extends AbstractFloraCoreCommand {
    public RealNameCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("realname <target>")
    @CommandDescription("获取指定玩家的真实昵称")
    public void realName(final @NotNull Player s, final @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        UUID tu = target.getUniqueId();
        Players ps = getStorageImplementation().selectPlayers(tu);
        String name = target.getDisplayName();
        String realName = ps.getName();
        Message.COMMAND_REALNAME_SUCCESS.send(sender, name, realName);
    }
}
