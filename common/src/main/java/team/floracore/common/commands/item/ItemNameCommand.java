package team.floracore.common.commands.item;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.Greedy;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;
import team.floracore.common.util.MultipleVersionsUtil;
import team.floracore.common.util.SenderUtil;
import team.floracore.common.util.StringUtil;

import java.util.Optional;

@CommandDescription("更改手上物品的名称")
@CommandPermission("floracore.command.itemname")
public class ItemNameCommand extends AbstractFloraCoreCommand {
    public ItemNameCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("itemname set <player> <name>")
    @CommandPermission("floracore.command.itemname.set")
    public void set(
            @NotNull CommandSender s,
            @NotNull @Argument("player") Player player,
            @NotNull @Greedy @Argument("name") String name,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        boolean self = !s.equals(player); // 指定目标是不是自己

        final String finalName = StringUtil.parseColour(name);
        if (self) {
            Optional<ItemStack> optItem = checkItemInHand(sender, player.getInventory(), null);
            if (optItem.isPresent()) {
                ItemStack item = optItem.get();
                ItemMeta meta = item.getItemMeta();
                //noinspection DataFlowIssue 已经检查过不会为null了
                meta.setDisplayName(finalName);
                item.setItemMeta(meta);
                Message.COMMAND_ITEMNAME_SET_SELF.send(sender, finalName);
            }
        } else {
            // 当目标不是自己时，要求有other权限
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.itemname.set.other")) {
                return;
            }
            Optional<ItemStack> optItem = checkItemInHand(sender, player.getInventory(), player.getName());
            if (optItem.isPresent()) {
                ItemStack item = optItem.get();
                ItemMeta meta = item.getItemMeta();
                //noinspection DataFlowIssue 已经检查过不会为null了
                meta.setDisplayName(finalName);
                item.setItemMeta(meta);
                Message.COMMAND_ITEMNAME_SET_OTHER.send(sender, player.getName(), finalName);
                if (silent == null || !silent) {
                    Message.COMMAND_ITEMNAME_SET_FROM.send(getPlugin().getSenderFactory().wrap(player), s.getName(), finalName);
                }
            }
        }
    }

    @CommandMethod("itemname reset <player>")
    @CommandPermission("floracore.command.itemname.reset")
    public void reset(
            @NotNull CommandSender s,
            @NotNull @Argument("player") Player player,
            @Nullable @Flag("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        boolean self = !s.equals(player); // 指定目标是不是自己

        if (self) {
            Optional<ItemStack> optItem = checkItemInHand(sender, player.getInventory(), null);
            if (optItem.isPresent()) {
                ItemStack item = optItem.get();
                ItemMeta meta = item.getItemMeta();
                //noinspection DataFlowIssue 已经检查过不会为null了
                meta.setDisplayName(null);
                item.setItemMeta(meta);
                Message.COMMAND_ITEMNAME_RESET_SELF.send(sender);
            }
        } else {
            // 当目标不是自己时，要求有other权限
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.itemname.reset.other")) {
                return;
            }
            Optional<ItemStack> optItem = checkItemInHand(sender, player.getInventory(), player.getName());
            if (optItem.isPresent()) {
                ItemStack item = optItem.get();
                ItemMeta meta = item.getItemMeta();
                //noinspection DataFlowIssue 已经检查过不会为null了
                meta.setDisplayName(null);
                item.setItemMeta(meta);
                Message.COMMAND_ITEMNAME_RESET_OTHER.send(sender, player.getName());
                if (silent == null || !silent) {
                    Message.COMMAND_ITEMNAME_RESET_FROM.send(getPlugin().getSenderFactory().wrap(player), s.getName());
                }
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
                Message.COMMAND_ITEMNAME_NOITEM_SELF.send(sender);
            } else {
                Message.COMMAND_ITEMNAME_NOITEM_OTHER.send(sender, other);
            }
            return Optional.empty();
        }
        @Nullable ItemMeta meta = item.getItemMeta();
        if (meta == null) { // 物品没有meta
            Message.COMMAND_ITEMNAME_UNSUPPORTEDITEM.send(sender);
            return Optional.empty();
        }
        return Optional.of(item);
    }
}
