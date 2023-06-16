package team.floracore.bungee.messaging;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import team.floracore.api.bungee.messenger.message.type.ChatMessage;
import team.floracore.api.bungee.messenger.message.type.KickMessage;
import team.floracore.api.bungee.messenger.message.type.NoticeMessage;
import team.floracore.api.data.chat.ChatType;
import team.floracore.api.messenger.message.Message;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.messaging.chat.ChatModel;
import team.floracore.bungee.config.chat.ChatKeys;
import team.floracore.bungee.locale.message.SocialSystemsMessage;
import team.floracore.bungee.messaging.message.ChatMessageImpl;
import team.floracore.bungee.messaging.message.KickMessageImpl;
import team.floracore.bungee.messaging.message.NoticeMessageImpl;
import team.floracore.bungee.util.BungeeStringReplacer;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.messaging.InternalMessagingService;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.common.sender.Sender;

import java.util.List;
import java.util.UUID;

public class BungeeMessagingFactory extends MessagingFactory<FCBungeePlugin> {
    public BungeeMessagingFactory(FCBungeePlugin plugin) {
        super(plugin);
    }

    @Override
    protected InternalMessagingService getServiceFor(String messagingType) {
        return super.getServiceFor(messagingType);
    }

    public boolean processIncomingMessage(String type, JsonElement content, UUID id) {
        // decode message
        Message decoded;
        switch (type) {
            case ChatMessageImpl.TYPE:
                decoded = ChatMessageImpl.decode(content, id);
                break;
            case NoticeMessageImpl.TYPE:
                decoded = NoticeMessageImpl.decode(content, id);
                break;
            case KickMessageImpl.TYPE:
                decoded = KickMessageImpl.decode(content, id);
                break;
            default:
                return false;

        }
        // consume the message
        processIncomingMessage(decoded);
        return true;
    }


    private void processIncomingMessage(Message message) {
        if (message instanceof ChatMessage) {
            ChatMessage chatMsg = (ChatMessage) message;
            chat(chatMsg);
        } else if (message instanceof NoticeMessage) {
            NoticeMessage noticeMsg = (NoticeMessage) message;
            notice(noticeMsg);
        } else if (message instanceof KickMessage) {
            KickMessage kickMsg = (KickMessage) message;
            kick(kickMsg);
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
        }
    }

    public void chat(ChatMessage chatMsg) {
        ProxiedPlayer player = getPlugin().getProxy().getPlayer(chatMsg.getReceiver());
        List<String> parameters = chatMsg.getParameters();
        UUID senderUUID = UUID.fromString(parameters.get(0));
        String senderName = getPlayerName(senderUUID);
        String message = parameters.get(1);
        switch (chatMsg.getType()) {
            case STAFF:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.socialsystems.staff")) {
                        SocialSystemsMessage.COMMAND_MISC_STAFF_CHAT.send(i, message, senderUUID);
                    }
                });
                break;
            case BUILDER:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.socialsystems.builder")) {
                        SocialSystemsMessage.COMMAND_MISC_BUILDER_CHAT.send(i, message, senderUUID);
                    }
                });
                break;
            case ADMIN:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.socialsystems.admin")) {
                        SocialSystemsMessage.COMMAND_MISC_ADMIN_CHAT.send(i, message, senderUUID);
                    }
                });
                break;
            case CUSTOM:
                String channel = parameters.get(2);
                ChatModel chatModel = null;
                List<ChatModel> chatModelList = getPlugin().getChatConfiguration().get(ChatKeys.CHAT_MODELS);
                for (ChatModel i : chatModelList) {
                    if (i.name.equalsIgnoreCase(channel)) {
                        chatModel = i;
                        break;
                    }
                }
                if (chatModel != null) {
                    ChatModel finalChatModel = chatModel;
                    getPlugin().getOnlineSenders().forEach(i -> {
                        if (i.hasPermission(finalChatModel.permission)) {
                            String prefix = BungeeStringReplacer.processStringForPlayer(i.getUniqueId(),
                                    finalChatModel.prefix);
                            Component pi = AbstractMessage.formatColoredValue(prefix);
                            SocialSystemsMessage.COMMAND_MISC_CUSTOM_CHAT.send(i, pi, message, senderUUID);
                        }
                    });
                }
                break;
        }
        if (player != null) {
            Sender sender = getPlugin().getSenderFactory().wrap(player);
            if (chatMsg.getType() == ChatType.PARTY) {
                SocialSystemsMessage.COMMAND_MISC_PARTY_CHAT.send(sender, message, senderUUID);
            }
        }
    }

    public void notice(NoticeMessage noticeMsg) {
        ProxiedPlayer player = getPlugin().getProxy().getPlayer(noticeMsg.getReceiver());
        List<String> parameters = noticeMsg.getParameters();
        if (player != null) {
            Sender sender = getPlugin().getSenderFactory().wrap(player);
            switch (noticeMsg.getType()) {
                case PARTY_ACCEPT:
                    UUID su1 = UUID.fromString(parameters.get(0));
                    UUID pu = UUID.fromString(parameters.get(1));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_ACCEPT.send(sender, su1, pu);
                    break;
                case PARTY_INVITE:
                    UUID su = UUID.fromString(parameters.get(0));
                    UUID tu1 = UUID.fromString(parameters.get(1));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE.send(sender, su, tu1);
                    break;
                case PARTY_INVITE_EXPIRED:
                    UUID tu2 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_EXPIRED.send(sender, tu2);
                    break;
                case PARTY_DISBAND:
                    UUID s = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_DISBAND.send(sender, s);
                    break;
                case PARTY_JOINED:
                    UUID s1 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_JOIN.send(sender, s1);
                    break;
                case PARTY_KICK:
                    UUID s3 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_KICK.send(sender, s3);
                    break;
                case PARTY_BE_KICKED:
                    UUID s4 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_BE_KICKED.send(sender, s4);
                    break;
                case PARTY_LEAVE:
                    UUID s5 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_LEAVE.send(sender, s5);
                    break;
                case PARTY_PROMOTE_LEADER:
                    UUID s6 = UUID.fromString(parameters.get(0));
                    UUID s7 = UUID.fromString(parameters.get(1));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_PROMOTE_LEADER.send(sender, s6, s7);
                    break;
                case PARTY_WARP_LEADER:
                    UUID s8 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_LEADER.send(sender, s8);
                    break;
                case PARTY_WARP_MODERATOR:
                    UUID s9 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_MODERATOR.send(sender, s9);
                    break;
                case PARTY_OFFLINE_LEADER:
                    UUID s10 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_LEADER.send(sender, s10);
                    break;
                case PARTY_OFFLINE:
                    UUID s11 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE.send(sender, s11);
                    break;
                case PARTY_OFFLINE_KICK:
                    UUID s12 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_KICK.send(sender, s12);
                    break;
                case PARTY_OFFLINE_TRANSFER:
                    UUID s13 = UUID.fromString(parameters.get(0));
                    UUID s14 = UUID.fromString(parameters.get(1));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_TRANSFER.send(sender, s13, s14);
                    break;
                case PARTY_DEMOTE:
                    UUID s15 = UUID.fromString(parameters.get(0));
                    UUID s16 = UUID.fromString(parameters.get(1));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_DEMOTE.send(sender, s15, s16);
                    break;
                case PARTY_PROMOTE_MODERATOR:
                    UUID s17 = UUID.fromString(parameters.get(0));
                    UUID s18 = UUID.fromString(parameters.get(1));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_PROMOTE_MODERATOR.send(sender, s17, s18);
                    break;
                case PARTY_OFFLINE_RE_ONLINE:
                    UUID s19 = UUID.fromString(parameters.get(0));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_RE_ONLINE.send(sender, s19);
                    break;
            }
        }
    }

    private void kick(KickMessage kickMessage) {
        UUID target = kickMessage.getReceiver();
        BaseComponent component = new TextComponent(ChatColor.translateAlternateColorCodes('&', kickMessage.getReason()));
        ProxiedPlayer player = getPlugin().getProxy().getPlayer(target);
        player.disconnect(component);
    }

    private String getPlayerName(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
    }

    public void pushChatMessage(UUID receiver, ChatType type, List<String> parameters) {
        this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                UUID requestId = service.generatePingId();
                this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
                ChatMessageImpl chatMessage = new ChatMessageImpl(requestId, receiver, type, parameters);
                service.getMessenger().sendOutgoingMessage(chatMessage);
                chat(chatMessage);
            });
        });
    }

    public void pushNoticeMessage(UUID receiver, NoticeMessage.NoticeType type, List<String> parameters) {
        this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                UUID requestId = service.generatePingId();
                this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
                NoticeMessageImpl noticeMessage = new NoticeMessageImpl(requestId, receiver, type, parameters);
                service.getMessenger().sendOutgoingMessage(noticeMessage);
                notice(noticeMessage);
            });
        });
    }

    private boolean isPlayerOnline(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().isOnline(uuid);
    }
}
