package team.floracore.bungee.chat;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.floracore.api.FloraCore;
import org.floracore.api.bungee.chat.ChannelsAPI;
import org.floracore.api.bungee.chat.ChatChannel;
import org.floracore.api.bungee.messenger.message.type.ChatMessage;
import org.floracore.api.data.DataType;
import team.floracore.bungee.config.ChatConfiguration;
import team.floracore.bungee.config.ChatKeys;
import team.floracore.bungee.messaging.BungeeMessagingFactory;
import team.floracore.common.storage.implementation.StorageImplementation;

import java.text.SimpleDateFormat;
import java.util.*;

public class Channels implements ChannelsAPI {
    private final CommandManager<CommandSender> manager;
    private final FloraCore api;
    private final StorageImplementation storage;
    private final BungeeMessagingFactory factory;
    private final List<ChatChannel> categories = new ArrayList<>();
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");

    public Channels(CommandManager<CommandSender> manager, ChatConfiguration chatConfiguration, FloraCore api, StorageImplementation storage, BungeeMessagingFactory factory) {
        this.manager = manager;
        this.api = api;
        this.storage = storage;
        this.factory = factory;
        chatConfiguration.get(ChatKeys.CHAT_CHANNELS).forEach(this::add);
    }

    @Override
    public ChatChannel parse(String identifierIn) throws IllegalArgumentException {
        for (ChatChannel category : categories) {
            for (String identifier : category.getIdentifiers()) {
                if (!identifier.equalsIgnoreCase(identifierIn)) {
                    continue;
                }
                return category;
            }
        }
        throw new IllegalArgumentException("No channel named '" + identifierIn + "' found!");
    }

    @Override
    public void add(ChatChannel chatChannel) {
        manager.command(manager.commandBuilder(chatChannel.getName(), chatChannel.getCommands(), this.manager.createDefaultCommandMeta())
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.of("message", StringArgument.StringMode.GREEDY))
                .handler(commandContext -> {
                    String messageIn = commandContext.get("message");
                    String message = chatChannel.getFormat()
                            .replace("%name%", chatChannel.getName())
                            .replace("%player%", api.getPlayerAPI().getPlayerRecordName(((ProxiedPlayer) commandContext.getSender()).getUniqueId()))
                            .replace("%time%", dateFormat.format(new Date()))
                            .replace("&r", "\247r")
                            .replace("&0", "\2470")
                            .replace("&1", "\2471")
                            .replace("&2", "\2472")
                            .replace("&3", "\2473")
                            .replace("&4", "\2474")
                            .replace("&5", "\2475")
                            .replace("&6", "\2476")
                            .replace("&7", "\2477")
                            .replace("&8", "\2478")
                            .replace("&9", "\2479")
                            .replace("&a", "\247a")
                            .replace("&b", "\247b")
                            .replace("&c", "\247c")
                            .replace("&d", "\247d")
                            .replace("&e", "\247e")
                            .replace("&f", "\247f")
                            .replace("&l", "\247l")
                            .replace("&n", "\247n")
                            .replace("&o", "\247o")
                            .replace("&k", "\247k")
                            .replace("&m", "\247m")
                            .replace("%message%", messageIn);
                    if (chatChannel.enableChatColor()) {
                        message = message.replace("&r", "\247r")
                                .replace("&0", "\2470")
                                .replace("&1", "\2471")
                                .replace("&2", "\2472")
                                .replace("&3", "\2473")
                                .replace("&4", "\2474")
                                .replace("&5", "\2475")
                                .replace("&6", "\2476")
                                .replace("&7", "\2477")
                                .replace("&8", "\2478")
                                .replace("&9", "\2479")
                                .replace("&a", "\247a")
                                .replace("&b", "\247b")
                                .replace("&c", "\247c")
                                .replace("&d", "\247d")
                                .replace("&e", "\247e")
                                .replace("&f", "\247f")
                                .replace("&l", "\247l")
                                .replace("&n", "\247n")
                                .replace("&o", "\247o")
                                .replace("&k", "\247k")
                                .replace("&m", "\247m");
                    }
                    factory.pushChatMessage(UUID.randomUUID(),
                            ChatMessage.ChatMessageType.CUSTOM,
                            Arrays.asList(((ProxiedPlayer) commandContext.getSender()).getUniqueId().toString(), message));
                })
        );

        manager.command(manager.commandBuilder(chatChannel.getName(), chatChannel.getCommands(), this.manager.createDefaultCommandMeta())
                .senderType(ProxiedPlayer.class)
                .handler(commandContext -> {
                    storage.insertData(((ProxiedPlayer) commandContext.getSender()).getUniqueId(),
                            DataType.FUNCTION,
                            "chat-channel",
                            chatChannel.getName(), 0);
                })
        );

        categories.add(chatChannel);
    }
}
