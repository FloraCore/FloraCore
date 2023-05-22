package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

/**
 * Fly命令
 */
@CommandPermission("floracore.command.fly")
@CommandDescription("设置玩家的飞行状态")
public class FlyCommand extends AbstractFloraCoreCommand implements Listener {
    public FlyCommand(FCBukkitPlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("fly")
    @CommandDescription("为自己开启飞行状态")
    public void self(final @NotNull Player s) {
        boolean old = s.getAllowFlight();
        s.setAllowFlight(!old);
        getAsyncExecutor().execute(() -> {
            if (whetherServerEnableAutoSync1()) {
                UUID uuid = s.getUniqueId();
                getStorageImplementation().insertData(uuid, DataType.AUTO_SYNC, "fly", String.valueOf(!old), 0);
            }
        });
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Message.COMMAND_FLY.send(sender, !old, s.getDisplayName());
    }

    @CommandMethod("fly <target>")
    @CommandPermission("floracore.command.fly.other")
    @CommandDescription("为别人开启飞行状态")
    public void other(final @NotNull CommandSender s, final @Argument("target") Player target, final @Nullable @Flag("silent") Boolean silent) {
        boolean old = target.getAllowFlight();
        target.setAllowFlight(!old);
        getAsyncExecutor().execute(() -> {
            if (whetherServerEnableAutoSync1()) {
                UUID uuid = target.getUniqueId();
                getStorageImplementation().insertData(uuid, DataType.AUTO_SYNC, "fly", String.valueOf(!old), 0);
            }
        });
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Sender targetSender = getPlugin().getSenderFactory().wrap(target);
        Message.COMMAND_FLY.send(sender, !old, target.getDisplayName());
        if (silent == null || !silent) {
            Message.COMMAND_FLY_FROM.send(targetSender, !old, sender.getDisplayName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID u = p.getUniqueId();
        if (whetherServerEnableAutoSync1()) {
            DATA data = getStorageImplementation().getSpecifiedData(u, DataType.AUTO_SYNC, "fly");
            if (data != null) {
                String value = data.getValue();
                boolean fly = Boolean.parseBoolean(value);
                if (fly && p.hasPermission("floracore.command.fly")) {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
            }
        }
    }
}
