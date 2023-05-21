package team.floracore.common.messaging;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.*;
import org.floracore.api.messenger.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.util.*;
import team.floracore.common.util.gson.*;

import java.util.*;
import java.util.concurrent.*;

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

    @Override
    public boolean consumeIncomingMessage(@NonNull Message message) {
        Objects.requireNonNull(message, "message");
        if (!this.receivedMessages.add(message.getId())) {
            return false;
        }

        // determine if the message can be handled by us
        //boolean valid = message instanceof;

        // instead of throwing an exception here, just return false
        // it means an instance of LP can gracefully handle messages it doesn't
        // "understand" yet. (sent from an instance running a newer version, etc)
        //if (!valid) {
        //    return false;
        //}

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
            default:
                // gracefully return if we just don't recognise the type
                return false;

        }

        // consume the message
        //processIncomingMessage(decoded);
        //return true;
    }

    private void processIncomingMessage(org.floracore.api.messenger.message.Message message) {
        /*if (!dispatchMessageReceiveEvent(message)) {
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
                taskId[0] = scheduler.runTaskTimerAsynchronously(plugin.getBootstrap().getPlugin(), new Runnable() {
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
        } else {*/
            throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
        // }
    }

    private String getPlayerName(UUID uuid) {
        return plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
    }

    private boolean isPlayerOnline(UUID uuid) {
        return plugin.getApiProvider().getPlayerAPI().isOnline(uuid);
    }

}
