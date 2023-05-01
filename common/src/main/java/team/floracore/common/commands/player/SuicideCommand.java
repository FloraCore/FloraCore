package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

@CommandPermission("floracore.command.suicide")
@CommandDescription("自杀")
public class SuicideCommand extends AbstractFloraCoreCommand {
    public SuicideCommand(FloraCorePlugin plugin) {
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
