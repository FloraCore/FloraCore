package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;

/**
 * Heal命令
 */
@CommandPermission("floracore.command.heal")
@CommandDescription("治疗一名玩家")
public class HealCommand extends FloraCoreBukkitCommand {
    public HealCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("heal")
    @CommandDescription("治疗您自己")
    public void self(@NotNull Player s) {
        heal(s);
        PlayerCommandMessage.COMMAND_HEAL_SELF.send(getPlugin().getSenderFactory().wrap(s));
    }

    @CommandMethod("heal <target>")
    @CommandDescription("治疗其他玩家")
    @CommandPermission("floracore.command.heal.other")
    public void other(@NotNull CommandSender s, @NotNull @Argument("target") Player target, @Nullable @Flag("silent") Boolean silent) {
        heal(target);
        PlayerCommandMessage.COMMAND_HEAL_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName());
        if (silent == null || !silent) {
            PlayerCommandMessage.COMMAND_HEAL_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName());
        }
    }

    private void heal(@NotNull Player player) {
        player.setHealth(player.getMaxHealth());
    }
}
