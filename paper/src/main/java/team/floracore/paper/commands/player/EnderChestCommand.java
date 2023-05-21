package team.floracore.paper.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;
import team.floracore.paper.*;
import team.floracore.paper.command.*;

import java.util.*;

/**
 * EnderChest命令
 */
@CommandDescription("打开末影箱")
@CommandPermission("floracore.command.enderchest")
public class EnderChestCommand extends AbstractFloraCoreCommand implements Listener {
    /**
     * 对应玩家禁止修改的内容
     */
    public static final Map<UUID, Inventory> READONLY_MAP = new HashMap<>();

    public EnderChestCommand(FCBukkitPlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("ender|enderchest [target] [for]")
    @CommandDescription("为某人或自己打开某人或自己的末影箱")
    public void enderChest(
            @NotNull CommandSender s,
            @Nullable @Argument(value = "target", description = "末影箱主人，默认自己") Player target,
            @Nullable @Argument(value = "for", description = "打开目标，默认自己") Player for_,
            @Nullable @Flag(value = "readonly", description = "本次是否为只读模式，即使拥有权限") Boolean readonly,
            @Nullable @Flag(value = "silent", description = "静音模式，不通知打开目标") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        // 先排除一些错误问题
        if (!(s instanceof Player)) {
            // 当发送者不是玩家时，target和for都不能为空
            if (target == null || for_ == null) {
                Message.COMMAND_ENDERCHEST_NOT_PLAYER.send(sender);
                return;
            }
        }

        // 带着上面的判断，已经不用害怕出现异常了
        if (target == null) { // 0个参数，为命令发送者打开自己的末影箱
            // 为玩家打开自己的末影箱，
            Player player = (Player) s;
            Inventory inventory = player.getEnderChest();
            player.openInventory(inventory);
            Message.COMMAND_ENDERCHEST_OPEN_SELF.send(sender);
            if ((readonly != null && readonly) || !player.hasPermission("floracore.command.enderchest.edit")) { // 禁止修改
                READONLY_MAP.put(player.getUniqueId(), inventory);
                Message.COMMAND_ENDERCHEST_READONLY_TO.send(sender);
            }
        } else if (for_ == null) { // 1个参数，为命令发送者打开末影箱主人的末影箱
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.enderchest.other")) {
                return;
            }
            Player player = (Player) s;
            Inventory inventory = target.getEnderChest();
            player.openInventory(inventory);
            Message.COMMAND_ENDERCHEST_OPEN_OTHER.send(sender, target.getName());
            if ((readonly != null && readonly) || !player.hasPermission("floracore.command.enderchest.edit")) { // 禁止修改
                READONLY_MAP.put(player.getUniqueId(), inventory);
                Message.COMMAND_ENDERCHEST_READONLY_TO.send(sender);
            }
        } else { // 2个参数，为打开目标打开末影箱主人的末影箱
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.enderchest.other") ||
                    SenderUtil.sendIfNoPermission(sender, "floracore.command.enderchest.for")) {
                return;
            }
            Inventory inventory = target.getEnderChest();
            for_.openInventory(inventory);
            Message.COMMAND_ENDERCHEST_OPEN_FOR.send(sender, target.getName(), for_.getName());
            if (silent == null || !silent) { // 不是静音模式，告诉打开目标
                Message.COMMAND_ENDERCHEST_OPEN_FROM.send(sender, s.getName(), target.getName());
            }
            if ((readonly != null && readonly) || !for_.hasPermission("floracore.command.enderchest.edit")) { // 禁止修改
                READONLY_MAP.put(for_.getUniqueId(), inventory);
                Message.COMMAND_ENDERCHEST_READONLY_TO.send(sender);
            }
        }
    }

    @EventHandler
    public void onDragInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory readOnlyInventory = READONLY_MAP.get(player.getUniqueId());
        Inventory clickedInventory = event.getClickedInventory();
        boolean cancel = false;
        if (readOnlyInventory != null) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || readOnlyInventory.equals(clickedInventory)) {
                cancel = true;
            }
        }
        if (cancel) {
            event.setCancelled(true);
            Message.COMMAND_ENDERCHEST_READONLY_FROM.send(getPlugin().getSenderFactory().wrap(player));
        }
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = READONLY_MAP.get(player.getUniqueId());
        if (Objects.equals(event.getInventory(), inventory)) { // 禁止修改这个物品栏
            event.setCancelled(true);
            Message.COMMAND_ENDERCHEST_READONLY_FROM.send(getPlugin().getSenderFactory().wrap(player));
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Inventory inventory = READONLY_MAP.get(event.getPlayer().getUniqueId());
        if (event.getInventory().equals(inventory)) { // 禁止修改这个物品栏
            // 状态结束，删掉本次会话
            READONLY_MAP.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        READONLY_MAP.remove(event.getPlayer().getUniqueId());
    }
}