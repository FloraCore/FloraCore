package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.api.data.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.implementation.*;

import java.util.*;

@CommandPermission("floracore.command.fly")
public class FlyCommand extends AbstractFloraCoreCommand {
    public FlyCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("fly")
    @CommandDescription("为自己开启飞行状态")
    public void self(final @NotNull Player s) {
        boolean old = s.getAllowFlight();
        s.setAllowFlight(!old);
        UUID uuid = s.getUniqueId();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        // 永不过期
        storageImplementation.insertData(uuid, DataType.AUTO_SYNC, "fly", String.valueOf(!old), 0);
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Message.COMMAND_FLY.send(sender, !old, s.getDisplayName());
    }

    @CommandMethod("fly <target>")
    @CommandPermission("floracore.command.fly.other")
    @CommandDescription("为别人开启飞行状态")
    public void other(final @NotNull CommandSender s, final @Argument("target") Player target, final @Nullable @Flag("silent") Boolean silent) {
        boolean old = target.getAllowFlight();
        target.setAllowFlight(!old);
        UUID uuid = target.getUniqueId();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        // 永不过期
        storageImplementation.insertData(uuid, DataType.AUTO_SYNC, "fly", String.valueOf(!old), 0);
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Sender targetSender = getPlugin().getSenderFactory().wrap(target);
        Message.COMMAND_FLY.send(sender, !old, target.getDisplayName());
        if (silent == null || !silent) {
            Message.COMMAND_FLY_FROM.send(targetSender, !old, sender.getDisplayName());
        }
    }
}
