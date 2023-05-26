package team.floracore.bungee;

import com.google.gson.*;
import net.md_5.bungee.api.connection.*;
import org.floracore.api.bungee.messenger.message.type.*;
import org.floracore.api.messenger.message.*;
import team.floracore.bungee.locale.message.*;
import team.floracore.bungee.messaging.message.*;
import team.floracore.common.messaging.*;
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
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
        }
    }

    public void chat(ChatMessage chatMsg) {
        ProxiedPlayer player = getPlugin().getBootstrap().getProxy().getPlayer(chatMsg.getReceiver());
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

    private boolean isPlayerOnline(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().isOnline(uuid);
    }
}
