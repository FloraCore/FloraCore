package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public class FlyCommand extends AbstractFloraCoreCommand {
    public FlyCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("fly")
    @CommandPermission("floracore.command.fly")
    @CommandDescription("为自己开启飞行状态")
    public void self(final @NotNull Player s) {
        boolean old = s.getAllowFlight();
        s.setAllowFlight(!old);
        // TODO 设置自动同步飞行状态
        UUID uuid = s.getUniqueId();
        getPlugin().getStorage().getImplementation().deleteDataExpired(uuid);
        // 永不过期
        getPlugin().getStorage().getImplementation().insertData(uuid, Data.DataType.AUTO_SYNC, "fly", String.valueOf(!old), 0);
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (old) {
            Message.COMMAND_FLY_DISABLE_SELF.send(sender);
        } else {
            Message.COMMAND_FLY_ENABLE_SELF.send(sender);
        }
    }

    @CommandMethod("fly <target>")
    @CommandPermission("floracore.command.fly.other")
    @CommandDescription("为别人开启飞行状态")
    public void other(final @NotNull CommandSender s, final @Argument("target") Player target, final @Nullable @Flag("silent") Boolean silent) {
        boolean old = target.getAllowFlight();
        target.setAllowFlight(!old);
        // TODO 设置自动同步飞行状态
        UUID uuid = target.getUniqueId();
        // 永不过期
        getPlugin().getStorage().getImplementation().insertData(uuid, Data.DataType.AUTO_SYNC, "fly", String.valueOf(!old), 0);
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Sender targetSender = getPlugin().getSenderFactory().wrap(target);
        if (old) {
            Message.COMMAND_FLY_DISABLE_OTHER.send(sender, target.getName());
            if (silent == null || !silent) {
                Message.COMMAND_FLY_DISABLE_FROM.send(targetSender, s.getName());
            }
        } else {
            Message.COMMAND_FLY_ENABLE_OTHER.send(sender, target.getName());
            if (silent == null || !silent) {
                Message.COMMAND_FLY_ENABLE_FROM.send(targetSender, s.getName());
            }
        }
    }
}
