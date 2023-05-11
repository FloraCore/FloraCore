package team.floracore.common.sender;

import net.kyori.adventure.platform.bukkit.*;
import net.kyori.adventure.text.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;

import java.util.*;

public class BukkitSenderFactory extends SenderFactory<FloraCorePlugin, CommandSender> {
    private final BukkitAudiences audiences;

    public BukkitSenderFactory(FloraCorePlugin plugin) {
        super(plugin);
        this.audiences = plugin.getBukkitAudiences();
    }

    @Override
    protected String getName(CommandSender sender) {
        if (sender instanceof Player) {
            return sender.getName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected String getDisplayName(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getDisplayName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        if (sender instanceof Player || sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                message = TranslationManager.render(message, uuid);
            }
            this.audiences.sender(sender).sendMessage(message);
        } else {
            Component finalMessage = message;
            getPlugin().getBootstrap().getScheduler().executeSync(() -> this.audiences.sender(sender).sendMessage(finalMessage));
        }
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        getPlugin().getBootstrap().getServer().dispatchCommand(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender;
    }

    @Override
    public void close() {
        super.close();
        this.audiences.close();
    }
}
