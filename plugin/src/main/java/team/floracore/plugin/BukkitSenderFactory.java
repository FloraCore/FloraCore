package team.floracore.plugin;

import net.kyori.adventure.platform.bukkit.*;
import net.kyori.adventure.text.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import team.floracore.common.sender.*;

import java.util.*;

public class BukkitSenderFactory extends SenderFactory<FCBukkitPlugin, CommandSender> {
    private final BukkitAudiences audiences;

    public BukkitSenderFactory(FCBukkitPlugin plugin) {
        super(plugin);
        this.audiences = BukkitAudiences.create(plugin.getLoader());
    }

    @Override
    protected String getName(CommandSender sender) {
        if (sender instanceof Player) {
            return sender.getName();
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
        // we can safely send async for players and the console - otherwise, send it sync
        if (sender instanceof Player || sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            this.audiences.sender(sender).sendMessage(message);
        } else {
            getPlugin().getBootstrap().getScheduler().executeSync(() -> this.audiences.sender(sender).sendMessage(message));
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
