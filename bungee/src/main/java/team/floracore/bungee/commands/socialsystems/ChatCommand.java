package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.specifier.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.google.common.collect.*;
import net.kyori.adventure.text.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.event.*;
import org.floracore.api.bungee.messenger.message.type.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.jetbrains.annotations.*;
import team.floracore.bungee.*;
import team.floracore.bungee.command.*;
import team.floracore.bungee.locale.message.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.chat")
public class ChatCommand extends FloraCoreBungeeCommand implements Listener {
    public ChatCommand(FCBungeePlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("chat <type>")
    @CommandDescription("Switches you to the <type> chat channel")
    public void chat(final @NotNull ProxiedPlayer player,
                     final @NotNull @Argument(value = "type", suggestions = "type") @Greedy String type) {
        UUID uuid = player.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(player);
        Type t = Type.parse(type);
        if (t == null) {
            SocialSystemsMessage.COMMAND_MISC_CHAT_DOES_NOT_EXIST.send(sender, type);
            return;
        }
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "chat");
        if (data != null) {
            Type nt = Type.valueOf(data.getValue());
            if (t == nt) {
                // TODO 你当前正处于 {0} 聊天频道中!
                SocialSystemsMessage.COMMAND_MISC_CHAT_IS_IN.send(sender, t.name().toLowerCase());
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
            case BLOGGER:
                if (!player.hasPermission("floracore.socialsystems.blogger")) {
                    MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                    return;
                }
                getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", t.name(), 0);
                Component tc1 = TranslationManager.render(MiscMessage.PREFIX_BLOGGER_LIGHT, uuid);
                SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc1);
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
            case ALL:
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
        }
    }

    @Suggestions("type")
    public List<String> getType(final @NotNull CommandContext<CommandSender> sender, final @NotNull String input) {
        List<String> ret = new ArrayList<>(Collections.singletonList(Type.ALL.name().toLowerCase()));
        CommandSender s = sender.getSender();
        ProxiedPlayer p = (ProxiedPlayer) s;
        if (p.hasPermission("floracore.socialsystems.admin")) {
            ret.add(Type.ADMIN.name().toLowerCase());
        }
        if (p.hasPermission("floracore.socialsystems.blogger")) {
            ret.add(Type.BLOGGER.name().toLowerCase());
        }
        if (p.hasPermission("floracore.socialsystems.builder")) {
            ret.add(Type.BUILDER.name().toLowerCase());
        }
        if (p.hasPermission("floracore.socialsystems.party")) {
            ret.add(Type.PARTY.name().toLowerCase());
        }
        if (p.hasPermission("floracore.socialsystems.staff")) {
            ret.add(Type.STAFF.name().toLowerCase());
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
            Type type = Type.valueOf(data.getValue());
            if (type == Type.ALL) {
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
                                Type.ALL.name(),
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
                                                    ChatMessage.ChatMessageType.PARTY,
                                                    Arrays.asList(uuid.toString(), message));
                                }
                            });
                            getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.PARTY, partyUUID.toString(), uuid, message, time));
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
                                    ChatMessage.ChatMessageType.ADMIN,
                                    Arrays.asList(uuid.toString(), message)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.ADMIN, "", uuid, message, time));
                    break;
                case STAFF:
                    if (!p.hasPermission("floracore.socialsystems.staff")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                            .pushChatMessage(UUID.randomUUID(),
                                    ChatMessage.ChatMessageType.STAFF,
                                    Arrays.asList(uuid.toString(), message)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.STAFF, "", uuid, message, time));
                    break;
                case BLOGGER:
                    if (!p.hasPermission("floracore.socialsystems.blogger")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                            .pushChatMessage(UUID.randomUUID(),
                                    ChatMessage.ChatMessageType.BLOGGER,
                                    Arrays.asList(uuid.toString(), message)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.BLOGGER, "", uuid, message, time));
                    break;
                case BUILDER:
                    if (!p.hasPermission("floracore.socialsystems.builder")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                            .pushChatMessage(UUID.randomUUID(),
                                    ChatMessage.ChatMessageType.BUILDER,
                                    Arrays.asList(uuid.toString(), message)));
                    getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.BUILDER, "", uuid, message, time));
                    break;
            }
        }
    }

    enum Type {
        ALL("all", "a"),
        PARTY("party", "p"),
        ADMIN("admin"),
        BLOGGER("blogger"),
        BUILDER("builder"),
        STAFF("STAFF", "s");
        private final List<String> identifiers;

        Type(String... identifiers) {
            this.identifiers = ImmutableList.copyOf(identifiers);
        }

        public static Type parse(String name) {
            for (Type t : values()) {
                for (String id : t.getIdentifiers()) {
                    if (id.equalsIgnoreCase(name)) {
                        return t;
                    }
                }
            }
            return null;
        }

        public List<String> getIdentifiers() {
            return this.identifiers;
        }
    }
}
