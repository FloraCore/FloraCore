package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.bukkit.util.Inventories;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.common.sender.Sender;

/**
 * Hat命令
 */
@CommandDescription("floracore.command.description.hat")
@CommandPermission("floracore.command.hat")
public class HatCommand extends FloraCoreBukkitCommand {
	public HatCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("hat")
	@CommandDescription("floracore.command.description.hat")
	public void hat(final @NotNull Player p) {
		Sender sender = getPlugin().getSenderFactory().wrap(p);
		PlayerInventory inv = p.getInventory();
		final ItemStack head = inv.getHelmet();
		final ItemStack hand = Inventories.getItemInMainHand(p);
		if (hand.getType() == Material.AIR) {
			PlayerCommandMessage.COMMAND_HAT_FAIL.send(sender);
			return;
		}

		if (hand.getType().getMaxDurability() != 0) {
			PlayerCommandMessage.COMMAND_HAT_ARMOR.send(sender);
			return;
		}
		if (BukkitWrapper.version >= 9 && head != null && head.getEnchantments()
				.containsKey(Enchantment.BINDING_CURSE) && !p.hasPermission(
				"floracore.command.hat.ignore-binding")) {
			PlayerCommandMessage.COMMAND_HAT_CURSE.send(sender);
			return;
		}
		inv.setHelmet(hand);
		Inventories.setItemInMainHand(p, head);
		PlayerCommandMessage.COMMAND_HAT_PLACED.send(sender);
	}

	@CommandMethod("hat remove")
	@CommandDescription("floracore.command.description.hat.remove")
	public void hatRemove(final @NotNull Player p) {
		Sender sender = getPlugin().getSenderFactory().wrap(p);
		PlayerInventory inv = p.getInventory();
		final ItemStack head = inv.getHelmet();
		if (head == null || head.getType() == Material.AIR) {
			PlayerCommandMessage.COMMAND_HAT_EMPTY.send(sender);
		} else if (BukkitWrapper.version >= 9 && head.getEnchantments()
				.containsKey(Enchantment.BINDING_CURSE) && !p.hasPermission(
				"floracore.command.hat.ignore-binding")) {
			PlayerCommandMessage.COMMAND_HAT_CURSE.send(sender);
		} else {
			final ItemStack air = new ItemStack(Material.AIR);
			inv.setHelmet(air);
			Inventories.addItem(p, head);
			PlayerCommandMessage.COMMAND_HAT_REMOVED.send(sender);
		}
	}
}