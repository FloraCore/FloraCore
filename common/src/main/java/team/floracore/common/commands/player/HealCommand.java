package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.util.*;

@CommandPermission("floracore.command.heal")
@CommandDescription("治疗一名玩家")
public class HealCommand extends AbstractFloraCoreCommand {
    public HealCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("heal")
    @CommandDescription("治疗您自己")
    public void self(@NotNull Player s) {
        heal(s);
        Message.COMMAND_HEAL_SELF.send(getPlugin().getSenderFactory().wrap(s));
    }

    @CommandMethod("heal <target>")
    @CommandDescription("治疗其他玩家")
    @CommandPermission("floracore.command.heal.other")
    public void other(
            @NotNull CommandSender s,
            @NotNull @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        heal(target);
        Message.COMMAND_HEAL_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName());
        if (silent == null || !silent) {
            Message.COMMAND_HEAL_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName());
        }
    }

    private void heal(@NotNull Player player) {
        player.setHealth(MultipleVersionsUtil.getMaxHealth(player));
    }
}
