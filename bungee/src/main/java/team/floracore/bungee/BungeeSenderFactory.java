package team.floracore.bungee;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.sender.Sender;
import team.floracore.common.sender.SenderFactory;

import java.util.UUID;

public class BungeeSenderFactory extends SenderFactory<FCBungeePlugin, CommandSender> {
    private final BungeeAudiences audiences;

    public BungeeSenderFactory(FCBungeePlugin plugin) {
        super(plugin);
        this.audiences = BungeeAudiences.create(plugin.getLoader());
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) sender).getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected String getName(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return sender.getName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected String getDisplayName(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) sender).getDisplayName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            UUID uuid = player.getUniqueId();
            message = TranslationManager.render(message, uuid);
            this.audiences.sender(sender).sendMessage(message);
        } else {
            Component finalMessage = message;
            getPlugin().getBootstrap()
                       .getScheduler()
                       .executeSync(() -> this.audiences.sender(sender).sendMessage(finalMessage));
        }
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        getPlugin().getProxy().getPluginManager().dispatchCommand(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return !(sender instanceof ProxiedPlayer);
    }

    @Override
    public void close() {
        super.close();
        this.audiences.close();
    }

    public BungeeAudiences getAudiences() {
        return audiences;
    }
}
