package team.floracore.bungee.chat;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.floracore.api.bungee.chat.ChannelsAPI;
import org.floracore.api.bungee.chat.ChatChannel;
import org.floracore.api.bungee.messenger.message.type.ChatMessage;
import org.floracore.api.data.DataType;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;
import team.floracore.bungee.config.ChatKeys;
import team.floracore.bungee.locale.message.SocialSystemsMessage;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Channels extends FloraCoreBungeeCommand implements ChannelsAPI {
    private static final List<ChatChannel> categories = new ArrayList<>();

    public Channels(FCBungeePlugin plugin) {
        super(plugin);
        plugin.getChatConfiguration().get(ChatKeys.CHAT_CHANNELS).forEach(this::add);
    }

    public static ChatChannel parse(String identifierIn) {
        for (ChatChannel category : categories) {
            for (String identifier : category.getIdentifiers()) {
                if (!identifier.equalsIgnoreCase(identifierIn)) {
                    continue;
                }
                return category;
            }
        }
        return null;
    }

    public static boolean hasChannelPermission(ChatChannel chatChannel, Sender sender) {
        if (chatChannel.getPermissions().isEmpty()) return true;
        boolean hasPermission = false;
        for (String permission : chatChannel.getPermissions()) {
            if (sender.hasPermission(permission)) {
                hasPermission = true;
                break;
            }
        }
        return hasPermission;
    }

    @Override
    public void add(ChatChannel chatChannel) {
        CommandManager<CommandSender> manager = getPlugin().getCommandManager().getManager();
        manager.command(manager.commandBuilder(chatChannel.getKey(), chatChannel.getCommands(), manager.createDefaultCommandMeta())
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.of("message", StringArgument.StringMode.GREEDY))
                .handler(commandContext -> {
                    UUID uuid = ((ProxiedPlayer) commandContext.getSender()).getUniqueId();
                    String messageIn = commandContext.get("message");
                    getPlugin().getBungeeMessagingFactory().pushChatMessage(UUID.randomUUID(),
                            ChatMessage.ChatMessageType.CUSTOM,
                            Arrays.asList(uuid.toString(), messageIn));
                })
        );

        manager.command(manager.commandBuilder(chatChannel.getKey(), chatChannel.getCommands(), manager.createDefaultCommandMeta())
                .senderType(ProxiedPlayer.class)
                .handler(commandContext -> {
                    UUID uuid = ((ProxiedPlayer) commandContext.getSender()).getUniqueId();
                    getStorageImplementation().insertData(uuid,
                            DataType.FUNCTION,
                            "chat-channel",
                            chatChannel.getKey(), 0);
                    SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(getPlugin().getSenderFactory().wrap(commandContext.getSender()),
                            Component.text(ChatColor.translateAlternateColorCodes('&', chatChannel.getName())));
                })
        );

        categories.add(chatChannel);
    }
}
