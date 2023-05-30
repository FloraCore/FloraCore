package team.floracore.bungee.messaging;

import com.google.gson.*;
import net.md_5.bungee.api.connection.*;
import org.floracore.api.bungee.messenger.message.type.*;
import org.floracore.api.messenger.message.*;
import org.floracore.api.messenger.message.type.*;
import team.floracore.bungee.*;
import team.floracore.bungee.locale.message.*;
import team.floracore.bungee.messaging.message.*;
import team.floracore.common.messaging.*;
import team.floracore.common.messaging.message.*;
import team.floracore.common.sender.*;

import java.util.*;

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
            case ChangeNameMessageImpl.TYPE:
                decoded = ChangeNameMessageImpl.decode(content, id);
                break;
            default:
                return false;

        }
        // consume the message
        processIncomingMessage(decoded);
        return true;
    }


    private void processIncomingMessage(org.floracore.api.messenger.message.Message message) {
        if (message instanceof ChatMessage) {
            ChatMessage chatMsg = (ChatMessage) message;
            chat(chatMsg);
        } else if (message instanceof NoticeMessage) {
            NoticeMessage noticeMsg = (NoticeMessage) message;
            notice(noticeMsg);
        } else if (message instanceof ChangeNameMessage) {
            ChangeNameMessage changeNameMsg = (ChangeNameMessage) message;
            UUID changer = changeNameMsg.getChanger();
            String name = changeNameMsg.getName();
            ProxiedPlayer player = getPlugin().getProxy().getPlayer(changer);
            if (player != null) {
                player.setDisplayName(name);
            }
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
        }
    }

    public void chat(ChatMessage chatMsg) {
        ProxiedPlayer player = getPlugin().getProxy().getPlayer(chatMsg.getReceiver());
        List<String> parameters = chatMsg.getParameters();
        switch (chatMsg.getType()) {
            case STAFF:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.chat.staff")) {
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_STAFF_CHAT.send(i, sn1, mess);
                    }
                });
                break;
            case BLOGGER:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.chat.blogger")) {
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_BLOGGER_CHAT.send(i, sn1, mess);
                    }
                });
                break;
            case BUILDER:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.chat.builder")) {
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_BUILDER_CHAT.send(i, sn1, mess);
                    }
                });
                break;
            case ADMIN:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.chat.admin")) {
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_ADMIN_CHAT.send(i, sn1, mess);
                    }
                });
                break;
        }
        if (player != null) {
            Sender sender = getPlugin().getSenderFactory().wrap(player);
            switch (chatMsg.getType()) {
                case PARTY:
                    UUID su1 = UUID.fromString(parameters.get(0));
                    String sn1 = getPlayerName(su1);
                    String mess = parameters.get(1);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_CHAT.send(sender, sn1, mess);
                    break;
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
                    String sn1 = getPlayerName(su1);
                    UUID pu = UUID.fromString(parameters.get(1));
                    SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_ACCEPT.send(sender, sn1, pu);
                    break;
                case PARTY_INVITE:
                    UUID su = UUID.fromString(parameters.get(0));
                    UUID tu1 = UUID.fromString(parameters.get(1));
                    String sn = getPlayerName(su);
                    String tn = getPlayerName(tu1);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE.send(sender, sn, tn);
                    break;
                case PARTY_INVITE_EXPIRED:
                    UUID tu2 = UUID.fromString(parameters.get(0));
                    String target = getPlayerName(tu2);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_EXPIRED.send(sender, target);
                    break;
                case PARTY_DISBAND:
                    UUID s = UUID.fromString(parameters.get(0));
                    String sn2 = getPlayerName(s);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_DISBAND.send(sender, sn2);
                    break;
                case PARTY_JOINED:
                    UUID s1 = UUID.fromString(parameters.get(0));
                    String sn3 = getPlayerName(s1);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_JOIN.send(sender, sn3);
                    break;
                case PARTY_KICK:
                    UUID s3 = UUID.fromString(parameters.get(0));
                    String sn4 = getPlayerName(s3);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_KICK.send(sender, sn4);
                    break;
                case PARTY_BE_KICKED:
                    UUID s4 = UUID.fromString(parameters.get(0));
                    String sn5 = getPlayerName(s4);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_BE_KICKED.send(sender, sn5);
                    break;
                case PARTY_LEAVE:
                    UUID s5 = UUID.fromString(parameters.get(0));
                    String sn6 = getPlayerName(s5);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_LEAVE.send(sender, sn6);
                    break;
                case PARTY_PROMOTE_LEADER:
                    UUID s6 = UUID.fromString(parameters.get(0));
                    UUID s7 = UUID.fromString(parameters.get(1));
                    String sn7 = getPlayerName(s6);
                    String sn8 = getPlayerName(s7);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_PROMOTE_LEADER.send(sender, sn7, sn8);
                    break;
                case PARTY_WARP_LEADER:
                    UUID s8 = UUID.fromString(parameters.get(0));
                    String sn9 = getPlayerName(s8);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_LEADER.send(sender, sn9);
                    break;
                case PARTY_WARP_MODERATOR:
                    UUID s9 = UUID.fromString(parameters.get(0));
                    String sn10 = getPlayerName(s9);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_MODERATOR.send(sender, sn10);
                    break;
                case PARTY_OFFLINE_LEADER:
                    UUID s10 = UUID.fromString(parameters.get(0));
                    String sn11 = getPlayerName(s10);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_LEADER.send(sender, sn11);
                    break;
                case PARTY_OFFLINE:
                    UUID s11 = UUID.fromString(parameters.get(0));
                    String sn12 = getPlayerName(s11);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE.send(sender, sn12);
                    break;
                case PARTY_OFFLINE_KICK:
                    UUID s12 = UUID.fromString(parameters.get(0));
                    String sn13 = getPlayerName(s12);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_KICK.send(sender, sn13);
                    break;
                case PARTY_OFFLINE_TRANSFER:
                    UUID s13 = UUID.fromString(parameters.get(0));
                    String sn14 = getPlayerName(s13);
                    UUID s14 = UUID.fromString(parameters.get(1));
                    String sn15 = getPlayerName(s14);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_TRANSFER.send(sender, sn14, sn15);
                    break;
                case PARTY_DEMOTE:
                    UUID s15 = UUID.fromString(parameters.get(0));
                    String sn16 = getPlayerName(s15);
                    UUID s16 = UUID.fromString(parameters.get(1));
                    String sn17 = getPlayerName(s16);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_DEMOTE.send(sender, sn16, sn17);
                    break;
                case PARTY_PROMOTE_MODERATOR:
                    UUID s17 = UUID.fromString(parameters.get(0));
                    String sn18 = getPlayerName(s17);
                    UUID s18 = UUID.fromString(parameters.get(1));
                    String sn19 = getPlayerName(s18);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_PROMOTE_MODERATOR.send(sender, sn18, sn19);
                    break;
                case PARTY_OFFLINE_RE_ONLINE:
                    UUID s19 = UUID.fromString(parameters.get(0));
                    String sn20 = getPlayerName(s19);
                    SocialSystemsMessage.COMMAND_MISC_PARTY_OFFLINE_RE_ONLINE.send(sender, sn20);
                    break;
            }
        }
    }

    private String getPlayerName(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
    }

    public void pushChatMessage(UUID receiver, ChatMessage.ChatMessageType type, List<String> parameters) {
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
