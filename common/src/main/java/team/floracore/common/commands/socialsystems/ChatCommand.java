package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.specifier.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.google.common.collect.*;
import net.kyori.adventure.text.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.data.*;
import org.floracore.api.messenger.message.type.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.chat")
public class ChatCommand extends AbstractFloraCoreCommand implements Listener {
    public ChatCommand(FloraCorePlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("chat <type>")
    public void chat(final @NonNull Player player, final @NonNull @Argument(value = "type", suggestions = "type") @Greedy String type) {
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
        Player p = (Player) s;
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
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        String message = e.getMessage();
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "chat");
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
                        getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "chat", Type.ALL.name(), 0);
                        Component tc4 = TranslationManager.render(MiscMessage.PREFIX_ALL_LIGHT, uuid);
                        SocialSystemsMessage.COMMAND_MISC_CHAT_SUCCESS.send(sender, tc4);
                    } else {
                        UUID partyUUID = UUID.fromString(cd.getValue());
                        PARTY party = getStorageImplementation().selectParty(partyUUID);
                        List<UUID> members = party.getMembers();
                        getAsyncExecutor().execute(() -> {
                            getPlugin().getMessagingService().ifPresent(service -> {
                                for (UUID member : members) {
                                    service.pushChatMessage(member, ChatMessage.ChatMessageType.PARTY, new String[]{uuid.toString(), message});
                                }
                            });
                        });
                    }
                    break;
                case ADMIN:
                    if (!p.hasPermission("floracore.socialsystems.admin")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> {
                        getPlugin().getMessagingService().ifPresent(service -> {
                            service.pushChatMessage(UUID.randomUUID(), ChatMessage.ChatMessageType.ADMIN, new String[]{uuid.toString(), message});
                        });
                    });
                    break;
                case STAFF:
                    if (!p.hasPermission("floracore.socialsystems.staff")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> {
                        getPlugin().getMessagingService().ifPresent(service -> {
                            service.pushChatMessage(UUID.randomUUID(), ChatMessage.ChatMessageType.STAFF, new String[]{uuid.toString(), message});
                        });
                    });
                    break;
                case BLOGGER:
                    if (!p.hasPermission("floracore.socialsystems.blogger")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> {
                        getPlugin().getMessagingService().ifPresent(service -> {
                            service.pushChatMessage(UUID.randomUUID(), ChatMessage.ChatMessageType.BLOGGER, new String[]{uuid.toString(), message});
                        });
                    });
                    break;
                case BUILDER:
                    if (!p.hasPermission("floracore.socialsystems.builder")) {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                        return;
                    }
                    getAsyncExecutor().execute(() -> {
                        getPlugin().getMessagingService().ifPresent(service -> {
                            service.pushChatMessage(UUID.randomUUID(), ChatMessage.ChatMessageType.BUILDER, new String[]{uuid.toString(), message});
                        });
                    });
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
