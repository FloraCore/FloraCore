package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.common.sender.*;

@CommandPermission("floracore.command.suicide")
@CommandDescription("自杀")
public class SuicideBukkitCommand extends FloraCoreBukkitCommand {
    public SuicideBukkitCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("suicide")
    @CommandDescription("自杀")
    public void suicide(@NotNull Player p) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        final EntityDamageEvent ede = new EntityDamageEvent(p, EntityDamageEvent.DamageCause.SUICIDE, Float.MAX_VALUE);
        getPlugin().getListenerManager().getPluginManager().callEvent(ede);
        ede.getEntity().setLastDamageCause(ede);
        p.setHealth(0);
        PlayerCommandMessage.COMMAND_SUICIDE.send(sender);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender s2 = getPlugin().getSenderFactory().wrap(player);
            PlayerCommandMessage.COMMAND_SUICIDE_BROADCAST.send(s2, p.getDisplayName());
        }
    }
}
