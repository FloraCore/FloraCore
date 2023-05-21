package team.floracore.plugin.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.plugin.*;
import team.floracore.plugin.command.*;

import java.util.*;

/**
 * RealName命令
 */
@CommandPermission("floracore.command.realname")
@CommandDescription("获取指定玩家的真实昵称")
public class RealNameCommand extends AbstractFloraCoreCommand {
    public RealNameCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("realname <target>")
    public void realName(final @NotNull Player s, final @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        UUID tu = target.getUniqueId();
        PLAYER ps = getStorageImplementation().selectPlayer(tu);
        String name = target.getDisplayName();
        String realName = ps.getName();
        Message.COMMAND_REALNAME_SUCCESS.send(sender, name, realName);
    }
}
