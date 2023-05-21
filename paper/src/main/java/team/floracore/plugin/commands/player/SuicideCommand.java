package team.floracore.plugin.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.plugin.*;
import team.floracore.plugin.command.*;

@CommandPermission("floracore.command.suicide")
@CommandDescription("自杀")
public class SuicideCommand extends AbstractFloraCoreCommand {
    public SuicideCommand(FCBukkitPlugin plugin) {
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
        Message.COMMAND_SUICIDE.send(sender);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender s2 = getPlugin().getSenderFactory().wrap(player);
            Message.COMMAND_SUICIDE_BROADCAST.send(s2, p.getDisplayName());
        }
    }
}
