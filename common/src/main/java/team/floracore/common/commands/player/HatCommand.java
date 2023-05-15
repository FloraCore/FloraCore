package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.craftbukkit.*;

/**
 * Hat命令
 */
@CommandPermission("floracore.command.hat")
@CommandDescription("戴上一些酷炫的帽子")
public class HatCommand extends AbstractFloraCoreCommand {
    public HatCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("hat")
    public void hat(final @NotNull Player p) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        PlayerInventory inv = p.getInventory();
        final ItemStack head = inv.getHelmet();
        final ItemStack hand = Inventories.getItemInMainHand(p);
        if (hand.getType() == Material.AIR) {
            Message.COMMAND_HAT_FAIL.send(sender);
            return;
        }

        if (hand.getType().getMaxDurability() != 0) {
            Message.COMMAND_HAT_ARMOR.send(sender);
            return;
        }
        if (Inventories.HAS_OFFHAND && head != null && head.getEnchantments().containsKey(Enchantment.BINDING_CURSE) && !p.hasPermission("floracore.command.hat.ignore-binding")) {
            Message.COMMAND_HAT_CURSE.send(sender);
            return;
        }
        inv.setHelmet(hand);
        Inventories.setItemInMainHand(p, head);
        Message.COMMAND_HAT_PLACED.send(sender);
    }

    @CommandMethod("hat remove")
    @CommandDescription("移除你现在戴的帽子")
    public void hatRemove(final @NotNull Player p) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        PlayerInventory inv = p.getInventory();
        final ItemStack head = inv.getHelmet();
        if (head == null || head.getType() == Material.AIR) {
            Message.COMMAND_HAT_EMPTY.send(sender);
        } else if (Inventories.HAS_OFFHAND && head.getEnchantments().containsKey(Enchantment.BINDING_CURSE) && !p.hasPermission("floracore.command.hat.ignore-binding")) {
            Message.COMMAND_HAT_CURSE.send(sender);
        } else {
            final ItemStack air = new ItemStack(Material.AIR);
            inv.setHelmet(air);
            Inventories.addItem(p, head);
            Message.COMMAND_HAT_REMOVED.send(sender);
        }
    }
}
