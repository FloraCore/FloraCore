package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import team.floracore.api.data.DataType;
import team.floracore.api.data.chat.ChatType;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;
import team.floracore.bungee.commands.socialsystems.chat.ChatModel;
import team.floracore.bungee.config.ChatKeys;
import team.floracore.bungee.locale.message.SocialSystemsMessage;
import team.floracore.bungee.util.BungeeStringReplacer;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;
import team.floracore.common.storage.misc.floracore.tables.PARTY;

import java.util.*;

@CommandContainer
@CommandDescription("floracore.command.description.chat")
@CommandPermission("floracore.socialsystems.chat")
public class ChatCommand extends FloraCoreBungeeCommand implements Listener {
    public ChatCommand(FCBungeePlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("chat <type>")
    @CommandDescription("floracore.command.description.chat.channel")
    public void chat(final @NotNull ProxiedPlayer player,
                     final @NotNull @Argument(value = "type", suggestions = "type") @Greedy String type) {
        UUID uuid = player.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(player);
        ChatType t = ChatType.parse(type);
        ChatModel chatModel = null;
        List<ChatModel> chatModelList = getPlugin().getChatConfiguration().get(ChatKeys.CHAT_MODELS);
        if (t == null) {
            for (ChatModel i : chatModelList) {
                for (String identifier : i.identifiers) {
                    if (player.hasPermission(i.permission)) {
                        if (type.equalsIgnoreCase(identifier) || type.equalsIgnoreCase(i.name)) {
                            t = ChatType.CUSTOM;
                            chatModel = i;
                            break;
                        }
                    }
                }
            }
            if (t == null) {
                SocialSystemsMessage.COMMAND_MISC_CHAT_DOES_NOT_EXIST.send(sender, type);
                return;
            }
        }
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "chat");
        ChatModel oldChatModel = null;
        if (data != null) {
            ChatType nt = ChatType.valueOf(data.getValue());
            if (t == nt) {
                if (nt == ChatType.CUSTOM) {
                    DATA channel = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "chat" +
                            "-custom-channel");
                    for (ChatModel i : chatModelList) {
                        if (i.name.equalsIgnoreCase(channel.getValue())) {
                            oldChatModel = i;
                        }
                    }
                    if (oldChatModel != null && chatModel != null) {
                        if (oldChatModel.name.equalsIgnoreCase(chatModel.name)) {
                            String prefix = BungeeStringReplacer.processStringForPlayer(player, chatModel.prefix);
                            Component i = AbstractMessage.formatColoredValue(prefix);
                            SocialSystemsMessage.COMMAND_MISC_CHAT_IS_IN.send(sender, i);
                            return;
                        }
                    }
                } else {
                    Component i = Component.empty();
                    switch (t) {
                        case BUILDER:
                            i = TranslationManager.render(MiscMessage.PREFIX_BUILDER_LIGHT, uuid);
                            break;
                        case STAFF:
                            i = TranslationManager.render(MiscMessage.PREFIX_STAFF_LIGHT, uuid);
                            break;
                        case PARTY:
                            i = TranslationManager.render(MiscMessage.PREFIX_PARTY_LIGHT, uuid);
                            break;
                        case SERVER:
                            i = TranslationManager.render(MiscMessage.PREFIX_ALL_LIGHT, uuid);
                            break;
                        case ADMIN:
                            i = TranslationManager.render(MiscMessage.PREFIX_ADMIN_LIGHT, uuid);
                            break;
                    }
                    SocialSystemsMessage.COMMAND_MISC_CHAT_IS_IN.send(sender, i);
                }
                return;
            }
        }
        switch (t) {
            case BUILDER:
                if (!player.hasPermission("floracore.socialsystems.builder")) {
                    MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                    return;
                }
                getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", t.name(), 0);
                Component tc = TranslationManager.render(MiscMessage.PREFIX_BUILDER_LIGHT, uuid);
                SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc);
                break;
            case STAFF:
                if (!player.hasPermission("floracore.socialsystems.staff")) {
                    MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                    return;
                }
                getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", t.name(), 0);
                Component tc2 = TranslationManager.render(MiscMessage.PREFIX_STAFF_LIGHT, uuid);
                SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc2);
                break;
            case PARTY:
                if (!player.hasPermission("floracore.socialsystems.party")) {
                    MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                    return;
                }
                DATA cd = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
                if (cd == null) {
                    SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
                } else {
                    getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", t.name(), 0);
                    Component tc3 = TranslationManager.render(MiscMessage.PREFIX_PARTY_LIGHT, uuid);
                    SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc3);
                }
                break;
            case SERVER:
                getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", t.name(), 0);
                Component tc4 = TranslationManager.render(MiscMessage.PREFIX_ALL_LIGHT, uuid);
                SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc4);
                break;
            case ADMIN:
                if (!player.hasPermission("floracore.socialsystems.admin")) {
                    MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                    return;
                }
                getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", t.name(), 0);
                Component tc5 = TranslationManager.render(MiscMessage.PREFIX_ADMIN_LIGHT, uuid);
                SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc5);
                break;
            case CUSTOM:
                if (chatModel == null) {
                    SocialSystemsMessage.COMMAND_MISC_CHAT_DOES_NOT_EXIST.send(sender, type);
                    return;
                }
                getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", t.name(), 0);
                getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat-custom-channel",
                        chatModel.name, 0);
                String prefix = BungeeStringReplacer.processStringForPlayer(player, chatModel.prefix);
                Component tc6 = AbstractMessage.formatColoredValue(prefix);
                SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc6);
                break;
        }
    }

    @Suggestions("type")
    public List<String> getType(final @NotNull CommandContext<CommandSender> sender, final @NotNull String input) {
        List<String> ret = new ArrayList<>(Collections.singletonList(ChatType.SERVER.getIdentifiers()
                .get(0)
                .toLowerCase()));
        CommandSender s = sender.getSender();
        ProxiedPlayer p = (ProxiedPlayer) s;
        if (p.hasPermission("floracore.socialsystems.admin")) {
            ret.add(ChatType.ADMIN.name().toLowerCase());
        }
        if (p.hasPermission("floracore.socialsystems.builder")) {
            ret.add(ChatType.BUILDER.name().toLowerCase());
        }
        if (p.hasPermission("floracore.socialsystems.party")) {
            ret.add(ChatType.PARTY.name().toLowerCase());
        }
        if (p.hasPermission("floracore.socialsystems.staff")) {
            ret.add(ChatType.STAFF.name().toLowerCase());
        }
        List<ChatModel> chatModelList = getPlugin().getChatConfiguration().get(ChatKeys.CHAT_MODELS);
        for (ChatModel chatModel : chatModelList) {
            if (p.hasPermission(chatModel.permission)) {
                ret.add(chatModel.name.toLowerCase());
            }
        }
        return ret;
    }

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        UUID uuid = p.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        String message = e.getMessage();
        if (message.startsWith("/")) {
            return;
        }
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "chat");
        long time = System.currentTimeMillis();
        if (data != null) {
            ChatType type = ChatType.valueOf(data.getValue());
            if (type == ChatType.SERVER) {
                return;
            }
            e.setCancelled(true);
            switch (type) {
                case PARTY:
                    if (!p.hasPermission("floracore.socialsystems.party")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    DATA cd = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
                    if (cd == null) {
                        SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
                        getStorageImplementation().insertData(uuid,
                                DataType.SOCIAL_SYSTEMS,
                                "chat",
                                ChatType.SERVER.name(),
                                0);
                        Component tc4 = TranslationManager.render(MiscMessage.PREFIX_ALL_LIGHT, uuid);
                        SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc4);
                    } else {
                        UUID partyUUID = UUID.fromString(cd.getValue());
                        PARTY party = getStorageImplementation().selectParty(partyUUID);
                        List<UUID> members = party.getMembers();
                        getAsyncExecutor().execute(() -> {
                            getPlugin().getMessagingService().ifPresent(service -> {
                                for (UUID member : members) {
                                    getPlugin().getBungeeMessagingFactory()
                                            .pushChatMessage(member,
                                                    ChatType.PARTY,
                                                    Arrays.asList(uuid.toString(), message));
                                }
                            });
                            getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.PARTY,
                                    partyUUID.toString(),
                                    uuid,
                                    message,
                                    time));
                        });
                    }
                    break;
                case ADMIN:
                    if (!p.hasPermission("floracore.socialsystems.admin")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                            .pushChatMessage(UUID.randomUUID(),
                                    ChatType.ADMIN,
                                    Arrays.asList(uuid.toString(),
                                            message)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.ADMIN,
                            "",
                            uuid,
                            message,
                            time));
                    break;
                case STAFF:
                    if (!p.hasPermission("floracore.socialsystems.staff")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                            .pushChatMessage(UUID.randomUUID(),
                                    ChatType.STAFF,
                                    Arrays.asList(uuid.toString(),
                                            message)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.STAFF,
                            "",
                            uuid,
                            message,
                            time));
                    break;
                case BUILDER:
                    if (!p.hasPermission("floracore.socialsystems.builder")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                            .pushChatMessage(UUID.randomUUID(),
                                    ChatType.BUILDER,
                                    Arrays.asList(uuid.toString(),
                                            message)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.BUILDER,
                            "",
                            uuid,
                            message,
                            time));
                    break;
                case CUSTOM:
                    DATA channel = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "chat" +
                            "-custom-channel");
                    ChatModel chatModel = null;
                    List<ChatModel> chatModelList = getPlugin().getChatConfiguration().get(ChatKeys.CHAT_MODELS);
                    for (ChatModel i : chatModelList) {
                        if (i.name.equalsIgnoreCase(channel.getValue())) {
                            if (!p.hasPermission(i.permission)) {
                                MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                                return;
                            }
                            chatModel = i;
                            break;
                        }
                    }
                    if (chatModel == null) {
                        SocialSystemsMessage.COMMAND_MISC_CHAT_DOES_NOT_EXIST.send(sender, channel.getValue());
                        Component tc4 = TranslationManager.render(MiscMessage.PREFIX_ALL_LIGHT, uuid);
                        SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc4);
                        return;
                    }
                    ChatModel finalChatModel = chatModel;
                    getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                            .pushChatMessage(UUID.randomUUID(),
                                    ChatType.CUSTOM,
                                    Arrays.asList(uuid.toString(), message,
                                            finalChatModel.name)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.CUSTOM,
                            finalChatModel.name,
                            uuid,
                            message,
                            time));
                    break;
            }
        }
    }
}
