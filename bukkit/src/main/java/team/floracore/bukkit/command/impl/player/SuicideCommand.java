package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;

@CommandDescription("floracore.command.description.suicide")
@CommandPermission("floracore.command.suicide")
public class SuicideCommand extends FloraCoreBukkitCommand {
    public SuicideCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("suicide")
    @CommandDescription("floracore.command.description.suicide")
    public void suicide(@NotNull Player p) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        final EntityDamageEvent ede = new EntityDamageEvent(p, EntityDamageEvent.DamageCause.SUICIDE, Float.MAX_VALUE);
        Bukkit.getPluginManager().callEvent(ede);
        ede.getEntity().setLastDamageCause(ede);
        p.setHealth(0);
        PlayerCommandMessage.COMMAND_SUICIDE.send(sender);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender s2 = getPlugin().getSenderFactory().wrap(player);
            PlayerCommandMessage.COMMAND_SUICIDE_BROADCAST.send(s2, p.getDisplayName());
        }
    }
}
