package team.floracore.common.messaging;

import com.google.gson.*;
import de.myzelyam.api.vanish.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.event.message.*;
import org.floracore.api.messenger.*;
import org.floracore.api.messenger.message.Message;
import org.floracore.api.messenger.message.type.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.messaging.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;
import team.floracore.common.util.gson.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class FloraCoreMessagingService implements InternalMessagingService, IncomingMessageConsumer {
    private final FloraCorePlugin plugin;
    private final ExpiringSet<UUID> receivedMessages;
    private final MessengerProvider messengerProvider;
    private final Messenger messenger;

    public FloraCoreMessagingService(FloraCorePlugin plugin, MessengerProvider messengerProvider) {
        this.plugin = plugin;

        this.messengerProvider = messengerProvider;
        this.messenger = messengerProvider.obtain(this);
        Objects.requireNonNull(this.messenger, "messenger");

        this.receivedMessages = new ExpiringSet<>(1, TimeUnit.HOURS);
    }

    public static String encodeMessageAsString(String type, UUID id, @Nullable JsonElement content) {
        JsonObject json = new JObject()
                .add("id", id.toString())
                .add("type", type)
                .consume(o -> {
                    if (content != null) {
                        o.add("content", content);
                    }
                })
                .toJson();
        return GsonProvider.normal().toJson(json);
    }

    @Override
    public String getName() {
        return this.messengerProvider.getName();
    }

    @Override
    public Messenger getMessenger() {
        return this.messenger;
    }

    @Override
    public MessengerProvider getMessengerProvider() {
        return this.messengerProvider;
    }

    @Override
    public void close() {
        this.messenger.close();
    }

    private UUID generatePingId() {
        UUID uuid = UUID.randomUUID();
        this.receivedMessages.add(uuid);
        return uuid;
    }

    public boolean dispatchMessageReceiveEvent(org.floracore.api.messenger.message.Message message) {
        MessageReceiveEvent event = new MessageReceiveEvent(plugin.getApiProvider(), message);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    @Override
    public void pushReport(UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer, String reason) {
        this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
            UUID requestId = generatePingId();
            this.plugin.getLogger().info("[Messaging] Sending ping with id: " + requestId);
            ReportMessageImpl reportMessage = new ReportMessageImpl(requestId, reporter, reportedUser, reporterServer, reportedUserServer, reason);
            this.messenger.sendOutgoingMessage(reportMessage);
            if (dispatchMessageReceiveEvent(reportMessage)) {
                String player = getPlayerName(reporter);
                String target = getPlayerName(reportedUser);
                boolean playerOnlineStatus = isPlayerOnline(reporter);
                boolean targetOnlineStatus = isPlayerOnline(reportedUser);
                notifyStaffReport(player, target, reporterServer, reportedUserServer, reason, playerOnlineStatus, targetOnlineStatus);
            }
        });
    }

    @Override
    public void pushTeleport(UUID sender, UUID recipient, String serverName) {
        this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
            UUID requestId = generatePingId();
            this.plugin.getLogger().info("[Messaging] Sending ping with id: " + requestId);
            TeleportMessageImpl teleportMessage = new TeleportMessageImpl(requestId, sender, recipient, serverName);
            this.messenger.sendOutgoingMessage(teleportMessage);
            /*if (dispatchMessageReceiveEvent(teleportMessage)) {

            }*/
        });
    }

    @Override
    public void pushConnectServer(UUID recipient, String serverName) {
        this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
            UUID requestId = generatePingId();
            this.plugin.getLogger().info("[Messaging] Sending ping with id: " + requestId);
            ConnectServerMessageImpl connectServerMessage = new ConnectServerMessageImpl(requestId, recipient, serverName);
            this.messenger.sendOutgoingMessage(connectServerMessage);
            /*if (dispatchMessageReceiveEvent(connectServerMessage)) {

            }*/
        });
    }

    @Override
    public void pushNoticeMessage(UUID receiver, NoticeMessage.NoticeType type, List<String> parameters) {
        this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
            UUID requestId = generatePingId();
            this.plugin.getLogger().info("[Messaging] Sending ping with id: " + requestId);
            NoticeMessageImpl noticeMessage = new NoticeMessageImpl(requestId, receiver, type, parameters);
            this.messenger.sendOutgoingMessage(noticeMessage);
            if (dispatchMessageReceiveEvent(noticeMessage)) {
                notice(noticeMessage);
            }
        });
    }

    @Override
    public void pushChatMessage(UUID receiver, ChatMessage.ChatMessageType type, List<String> parameters) {
        this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
            UUID requestId = generatePingId();
            this.plugin.getLogger().info("[Messaging] Sending ping with id: " + requestId);
            ChatMessageImpl chatMessage = new ChatMessageImpl(requestId, receiver, type, parameters);
            this.messenger.sendOutgoingMessage(chatMessage);
            if (dispatchMessageReceiveEvent(chatMessage)) {
                chat(chatMessage);
            }
        });
    }

    public void notifyStaffReport(String reporter, String reportedUser, String reporterServer, String reportedUserServer, String reason, boolean playerOnlineStatus, boolean targetOnlineStatus) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("floracore.socialsystems.staff")) {
                Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                team.floracore.common.locale.message.Message.COMMAND_MISC_REPORT_BROADCAST.send(s, reporter, reportedUser, reporterServer, reportedUserServer, reason, playerOnlineStatus, targetOnlineStatus);
            }
        }
    }

    public void chat(ChatMessage chatMsg) {
        Player player = Bukkit.getPlayer(chatMsg.getReceiver());
        List<String> parameters = chatMsg.getParameters();
        switch (chatMsg.getType()) {
            case STAFF:
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("floracore.chat.staff")) {
                        Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_STAFF_CHAT.send(s, sn1, mess);
                    }
                }
                break;
            case BLOGGER:
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("floracore.chat.blogger")) {
                        Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_BLOGGER_CHAT.send(s, sn1, mess);
                    }
                }
                break;
            case BUILDER:
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("floracore.chat.builder")) {
                        Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_BUILDER_CHAT.send(s, sn1, mess);
                    }
                }
                break;
            case ADMIN:
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("floracore.chat.admin")) {
                        Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                        UUID su1 = UUID.fromString(parameters.get(0));
                        String sn1 = getPlayerName(su1);
                        String mess = parameters.get(1);
                        SocialSystemsMessage.COMMAND_MISC_ADMIN_CHAT.send(s, sn1, mess);
                    }
                }
                break;
        }
        if (player != null) {
            Sender sender = plugin.getSenderFactory().wrap(player);
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
        Player player = Bukkit.getPlayer(noticeMsg.getReceiver());
        List<String> parameters = noticeMsg.getParameters();
        switch (noticeMsg.getType()) {
            case REPORT_STAFF_ACCEPTED:
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("floracore.report.staff")) {
                        Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                        team.floracore.common.locale.message.Message.COMMAND_MISC_REPORT_NOTICE_STAFF_ACCEPTED.send(s, parameters.get(0), parameters.get(1));
                    }
                }
                break;
            case REPORT_STAFF_PROCESSED:
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("floracore.report.staff")) {
                        Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                        team.floracore.common.locale.message.Message.COMMAND_MISC_REPORT_NOTICE_STAFF_PROCESSED.send(s, parameters.get(0), parameters.get(1));
                    }
                }
                break;
        }
        if (player != null) {
            Sender sender = plugin.getSenderFactory().wrap(player);
            switch (noticeMsg.getType()) {
                case REPORT_ACCEPTED:
                    team.floracore.common.locale.message.Message.COMMAND_MISC_REPORT_NOTICE_ACCEPTED.send(sender, parameters.get(0));
                    team.floracore.common.locale.message.Message.COMMAND_MISC_REPORT_THANKS.send(sender);
                    break;
                case REPORT_PROCESSED:
                    team.floracore.common.locale.message.Message.COMMAND_MISC_REPORT_NOTICE_PROCESSED.send(sender, parameters.get(0));
                    team.floracore.common.locale.message.Message.COMMAND_MISC_REPORT_THANKS.send(sender);
                    break;
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
            }
        }
    }

    @Override
    public boolean consumeIncomingMessage(@NonNull Message message) {
        Objects.requireNonNull(message, "message");
        if (!this.receivedMessages.add(message.getId())) {
            return false;
        }

        // determine if the message can be handled by us
        boolean valid = message instanceof ReportMessage;

        // instead of throwing an exception here, just return false
        // it means an instance of LP can gracefully handle messages it doesn't
        // "understand" yet. (sent from an instance running a newer version, etc)
        if (!valid) {
            return false;
        }

        processIncomingMessage(message);
        return true;
    }

    @Override
    public boolean consumeIncomingMessageAsString(@NonNull String encodedString) {
        try {
            return consumeIncomingMessageAsString0(encodedString);
        } catch (Exception e) {
            this.plugin.getLogger().warn("Unable to decode incoming messaging service message: '" + encodedString + "'", e);
            return false;
        }
    }

    private boolean consumeIncomingMessageAsString0(@NonNull String encodedString) {
        Objects.requireNonNull(encodedString, "encodedString");
        JsonObject parsed = Objects.requireNonNull(GsonProvider.normal().fromJson(encodedString, JsonObject.class), "parsed");
        JsonObject json = parsed.getAsJsonObject();

        // extract id
        JsonElement idElement = json.get("id");
        if (idElement == null) {
            throw new IllegalStateException("Incoming message has no id argument: " + encodedString);
        }
        UUID id = UUID.fromString(idElement.getAsString());

        // ensure the message hasn't been received already
        if (!this.receivedMessages.add(id)) {
            return false;
        }

        // extract type
        JsonElement typeElement = json.get("type");
        if (typeElement == null) {
            throw new IllegalStateException("Incoming message has no type argument: " + encodedString);
        }
        String type = typeElement.getAsString();

        // extract content
        @Nullable JsonElement content = json.get("content");

        // decode message
        org.floracore.api.messenger.message.Message decoded;
        switch (type) {
            case ReportMessageImpl.TYPE:
                decoded = ReportMessageImpl.decode(content, id);
                break;
            case NoticeMessageImpl.TYPE:
                decoded = NoticeMessageImpl.decode(content, id);
                break;
            case TeleportMessageImpl.TYPE:
                decoded = TeleportMessageImpl.decode(content, id);
                break;
            case ChatMessageImpl.TYPE:
                decoded = ChatMessageImpl.decode(content, id);
                break;
            case ConnectServerMessageImpl.TYPE:
                decoded = ConnectServerMessageImpl.decode(content, id);
                break;
            default:
                // gracefully return if we just don't recognise the type
                return false;

        }

        // consume the message
        processIncomingMessage(decoded);
        return true;
    }

    private void processIncomingMessage(org.floracore.api.messenger.message.Message message) {
        if (!dispatchMessageReceiveEvent(message)) {
            return;
        }

        if (message instanceof ReportMessage) {
            ReportMessage reportMsg = (ReportMessage) message;
            UUID reporter = reportMsg.getReporter();
            UUID reportedUser = reportMsg.getReportedUser();
            String reporterServer = reportMsg.getReporterServer();
            String reportedUserServer = reportMsg.getReportedUserServer();
            String reason = reportMsg.getReason();
            String player = getPlayerName(reporter);
            String target = getPlayerName(reportedUser);
            boolean playerOnline = isPlayerOnline(reporter);
            boolean targetOnline = isPlayerOnline(reportedUser);
            notifyStaffReport(player, target, reporterServer, reportedUserServer, reason, playerOnline, targetOnline);
        } else if (message instanceof NoticeMessage) {
            NoticeMessage noticeMsg = (NoticeMessage) message;
            notice(noticeMsg);
        } else if (message instanceof TeleportMessage) {
            TeleportMessage teleportMsg = (TeleportMessage) message;
            UUID su = teleportMsg.getSender();
            UUID ru = teleportMsg.getRecipient();
            String serverName = teleportMsg.getServerName();
            if (serverName.equalsIgnoreCase(plugin.getServerName())) {
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                AtomicBoolean shouldCancel = new AtomicBoolean(false);
                final int[] taskId = new int[1];
                taskId[0] = scheduler.runTaskTimer(plugin.getBootstrap().getPlugin(), new Runnable() {
                    private int secondsElapsed = 0;

                    public void run() {
                        Player sender = Bukkit.getPlayer(su);
                        Player recipient = Bukkit.getPlayer(ru);
                        Sender s = plugin.getSenderFactory().wrap(sender);
                        if (shouldCancel.get() || secondsElapsed >= 30 || recipient == null) {
                            // 取消任务
                            scheduler.cancelTask(taskId[0]);
                            return;
                        }
                        if (sender != null) {
                            if (plugin.isPluginInstalled("PremiumVanish")) {
                                if (!VanishAPI.isInvisible(sender)) {
                                    VanishAPI.hidePlayer(sender);
                                }
                            }
                            sender.teleport(recipient.getLocation());
                            team.floracore.common.locale.message.Message.COMMAND_REPORT_TP_SUCCESS.send(s, recipient.getDisplayName());
                            shouldCancel.set(true);
                        }
                        secondsElapsed++;
                    }
                }, 0L, 20L).getTaskId();
            }
        } else if (message instanceof ChatMessage) {
            ChatMessage chatMsg = (ChatMessage) message;
            chat(chatMsg);
        } else if (message instanceof ConnectServerMessage) {
            ConnectServerMessage connectServerMsg = (ConnectServerMessage) message;
            UUID ru = connectServerMsg.getRecipient();
            Player rp = Bukkit.getPlayer(ru);
            if (rp != null) {
                String serverName = connectServerMsg.getServerName();
                if (!serverName.equalsIgnoreCase(plugin.getServerName())) {
                    plugin.getBungeeUtil().connect(rp, serverName);
                }
            }
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
        }
    }

    private String getPlayerName(UUID uuid) {
        return plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
    }

    private boolean isPlayerOnline(UUID uuid) {
        return plugin.getApiProvider().getPlayerAPI().isOnline(uuid);
    }

}
