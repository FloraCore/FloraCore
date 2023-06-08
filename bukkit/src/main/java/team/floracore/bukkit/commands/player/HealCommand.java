package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;

/**
 * Heal命令
 */
@CommandDescription("floracore.command.description.heal")
@CommandPermission("floracore.command.heal")
public class HealCommand extends FloraCoreBukkitCommand {
    public HealCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("heal")
    @CommandDescription("floracore.command.description.heal.self")
    public void self(@NotNull Player s) {
        heal(s);
        PlayerCommandMessage.COMMAND_HEAL_SELF.send(getPlugin().getSenderFactory().wrap(s));
    }

    private void heal(@NotNull Player player) {
        player.setHealth(player.getMaxHealth());
    }

    @CommandMethod("heal <target>")
    @CommandDescription("floracore.command.description.heal.other")
    @CommandPermission("floracore.command.heal.other")
    public void other(@NotNull CommandSender s,
                      @NotNull @Argument("target") Player target,
                      @Nullable @Flag("silent") Boolean silent) {
        heal(target);
        PlayerCommandMessage.COMMAND_HEAL_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName());
        if (silent == null || !silent) {
            PlayerCommandMessage.COMMAND_HEAL_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName());
        }
    }
}
