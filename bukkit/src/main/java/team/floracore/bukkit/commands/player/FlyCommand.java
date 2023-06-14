package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.api.data.DataType;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;

import java.util.UUID;

/**
 * Fly命令
 */
@CommandPermission("floracore.command.fly")
@CommandDescription("floracore.command.description.fly")
public class FlyCommand extends FloraCoreBukkitCommand implements Listener {
    public FlyCommand(FCBukkitPlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("fly")
    @CommandDescription("floracore.command.description.fly.set")
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
        PlayerCommandMessage.COMMAND_FLY.send(sender, !old, s.getDisplayName());
    }

    @CommandMethod("fly <target>")
    @CommandDescription("floracore.command.description.fly.set.other")
    @CommandPermission("floracore.command.fly.other")
    public void other(final @NotNull CommandSender s,
                      final @Argument("target") Player target,
                      final @Nullable @Flag("silent") Boolean silent) {
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
        PlayerCommandMessage.COMMAND_FLY.send(sender, !old, target.getDisplayName());
        if (silent == null || !silent) {
            PlayerCommandMessage.COMMAND_FLY_FROM.send(targetSender, !old, sender.getDisplayName());
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
