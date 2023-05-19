package team.floracore.common.commands.item;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

import java.util.*;

@CommandPermission("floracore.command.itemflag")
@CommandDescription("给手上的物品添加或删除Flag")
public class ItemFlagCommand extends AbstractFloraCoreCommand {
    public ItemFlagCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("itemflag clear [target]")
    @CommandDescription("给手上的物品清空Flag")
    @CommandPermission("floracore.command.itemflag.clear")
    public void clear(
            @NotNull CommandSender s,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) {
            if (!(s instanceof Player)) {
                SenderUtil.sendMustBePlayer(sender, s.getClass());
                return;
            }
            Player player = (Player) s;
            PlayerInventory inventory = player.getInventory();
            // 检查物品是否存在（即手上有物品）且可修改（即拥有ItemMeta）
            Optional<ItemStack> optItem = checkItemInHand(sender, inventory, null);
            if (!optItem.isPresent()) { // 检查未通过
                return;
            }
            ItemStack item = optItem.get();
            ItemMeta meta = item.getItemMeta();
            // 由于getItemFlags是不可修改的集合，不用害怕快速失败
            //noinspection DataFlowIssue 已经检查过不会为null了
            meta.getItemFlags().forEach(meta::removeItemFlags);
            item.setItemMeta(meta);
            Message.COMMAND_ITEMFLAG_CLEAR_SELF.send(sender);
        } else {
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.itemflag.clear.other")) {
                return;
            }
            PlayerInventory inventory = target.getInventory();
            // 检查物品是否存在（即手上有物品）且可修改（即拥有ItemMeta）
            Optional<ItemStack> optItem = checkItemInHand(sender, inventory, target.getName());
            if (!optItem.isPresent()) { // 检查未通过
                return;
            }
            ItemStack item = optItem.get();
            ItemMeta meta = item.getItemMeta();
            // 由于getItemFlags是不可修改的集合，不用害怕快速失败
            //noinspection DataFlowIssue 已经检查过不会为null了
            meta.getItemFlags().forEach(meta::removeItemFlags);
            item.setItemMeta(meta);
            Message.COMMAND_ITEMFLAG_CLEAR_OTHER.send(sender, target.getName());
            if (silent == null || !silent) {
                Message.COMMAND_ITEMFLAG_CLEAR_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName());
            }
        }
    }

    @CommandMethod("itemflag add <flag> [target]")
    @CommandDescription("给手上的物品添加Flag")
    @CommandPermission("floracore.command.itemflag.add")
    public void add(
            @NotNull CommandSender s,
            @NotNull @Argument("flag") ItemFlag flag,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) {
            if (!(s instanceof Player)) {
                SenderUtil.sendMustBePlayer(sender, s.getClass());
                return;
            }
            Player player = (Player) s;
            PlayerInventory inventory = player.getInventory();
            Optional<ItemStack> optItem = checkItemInHand(sender, inventory, null);
            if (!optItem.isPresent()) { // 检查未通过
                return;
            }
            ItemStack item = optItem.get();
            ItemMeta meta = item.getItemMeta();
            //noinspection DataFlowIssue 已经检查过不会为null了
            if (meta.getItemFlags().contains(flag)) {
                Message.COMMAND_ITEMFLAG_ALREADY_HAS_SELF.send(sender, flag.name());
                return;
            }
            meta.addItemFlags(flag);
            item.setItemMeta(meta);
            Message.COMMAND_ITEMFLAG_REMOVE_SELF.send(sender, flag.name());
        } else {
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.itemflag.add.other")) {
                return;
            }
            PlayerInventory inventory = target.getInventory();
            Optional<ItemStack> optItem = checkItemInHand(sender, inventory, target.getName());
            if (!optItem.isPresent()) { // 检查未通过
                return;
            }
            ItemStack item = optItem.get();
            ItemMeta meta = item.getItemMeta();
            Sender targetSender = getPlugin().getSenderFactory().wrap(target);
            //noinspection DataFlowIssue 已经检查过不会为null了
            if (meta.getItemFlags().contains(flag)) {
                Message.COMMAND_ITEMFLAG_ALREADY_HAS_OTHER.send(sender, target.getName(), flag.name());
                return;
            }
            meta.addItemFlags(flag);
            item.setItemMeta(meta);
            Message.COMMAND_ITEMFLAG_ADD_OTHER.send(sender, target.getName(), flag.name());
            if (silent == null || !silent) {
                Message.COMMAND_ITEMFLAG_ADD_FROM.send(targetSender, s.getName(), flag.name());
            }
        }
    }

    @CommandMethod("itemflag remove <flag> [target]")
    @CommandDescription("给手上的物品移除Flag")
    @CommandPermission("floracore.command.itemflag.remove")
    public void remove(
            @NotNull CommandSender s,
            @NotNull @Argument("flag") ItemFlag flag,
            @Nullable @Argument("target") Player target,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) {
            if (!(s instanceof Player)) {
                SenderUtil.sendMustBePlayer(sender, s.getClass());
                return;
            }
            Player player = (Player) s;
            PlayerInventory inventory = player.getInventory();
            Optional<ItemStack> optItem = checkItemInHand(sender, inventory, null);
            if (!optItem.isPresent()) { // 检查未通过
                return;
            }
            ItemStack item = optItem.get();
            ItemMeta meta = item.getItemMeta();
            //noinspection DataFlowIssue 已经检查过不会为null了
            if (!meta.getItemFlags().contains(flag)) {
                Message.COMMAND_ITEMFLAG_HAS_NO_SELF.send(sender, flag.name());
                return;
            }
            meta.removeItemFlags(flag);
            item.setItemMeta(meta);
            Message.COMMAND_ITEMFLAG_REMOVE_SELF.send(sender, flag.name());
        } else {
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.itemflag.remove.other")) {
                return;
            }
            PlayerInventory inventory = target.getInventory();
            Optional<ItemStack> optItem = checkItemInHand(sender, inventory, target.getName());
            if (!optItem.isPresent()) { // 检查未通过
                return;
            }
            ItemStack item = optItem.get();
            ItemMeta meta = item.getItemMeta();
            Sender targetSender = getPlugin().getSenderFactory().wrap(target);
            //noinspection DataFlowIssue 已经检查过不会为null了
            if (!meta.getItemFlags().contains(flag)) {
                Message.COMMAND_ITEMFLAG_HAS_NO_OTHER.send(sender, target.getName(), flag.name());
                return;
            }
            meta.removeItemFlags(flag);
            item.setItemMeta(meta);
            Message.COMMAND_ITEMFLAG_REMOVE_OTHER.send(sender, target.getName(), flag.name());
            if (silent == null || !silent) {
                Message.COMMAND_ITEMFLAG_REMOVE_FROM.send(targetSender, s.getName(), flag.name());
            }
        }
    }

    /**
     * 检查玩家手上的物品，若物品不正确，则发送消息
     *
     * @param sender    命令发送者
     * @param inventory 物品栏
     * @param other     根据是否是他人发送对应的消息，当为null时发送self消息；当不为null时，发送other消息
     * @return 若检查通过，返回ItemStack，否则返回empty
     */
    private @NotNull Optional<ItemStack> checkItemInHand(@NotNull Sender sender, @NotNull PlayerInventory inventory, @Nullable String other) {
        @Nullable ItemStack item = MultipleVersionsUtil.getItemInMainHand(inventory);
        if (item == null || item.getType() == Material.AIR) { // 手上未持有任何物品
            if (other == null) {
                Message.COMMAND_ITEMFLAG_NOITEM_SELF.send(sender);
            } else {
                Message.COMMAND_ITEMFLAG_NOITEM_OTHER.send(sender, other);
            }
            return Optional.empty();
        }
        @Nullable ItemMeta meta = item.getItemMeta();
        if (meta == null) { // 物品没有meta
            Message.COMMAND_ITEMFLAG_UNSUPPORTED_ITEM.send(sender);
            return Optional.empty();
        }
        return Optional.of(item);
    }
}
