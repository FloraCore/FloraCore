package team.floracore.common.messaging;

import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.event.message.*;
import org.floracore.api.messenger.*;
import org.floracore.api.messenger.message.*;
import org.floracore.api.messenger.message.type.*;
import team.floracore.common.messaging.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
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

    public boolean dispatchMessageReceiveEvent(Message message) {
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
                String player = plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(reporter);
                String target = plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(reportedUser);
                notifyStaffReport(player, target, reporterServer, reportedUserServer, reason);
            }
        });
    }

    public void notifyStaffReport(String reporter, String reportedUser, String reporterServer, String reportedUserServer, String reason) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("floracore.report.staff")) {
                Sender s = plugin.getSenderFactory().wrap(onlinePlayer);
                team.floracore.common.locale.Message.COMMAND_MISC_REPORT_BROADCAST.send(s, reporter, reportedUser, reason, reporterServer, reportedUserServer);
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
        Message decoded;
        if (type.equals(ReportMessageImpl.TYPE)) {
            decoded = ReportMessageImpl.decode(content, id);
        } else {// gracefully return if we just don't recognise the type
            return false;
        }

        // consume the message
        processIncomingMessage(decoded);
        return true;
    }

    private void processIncomingMessage(Message message) {
        if (dispatchMessageReceiveEvent(message)) {
            if (message instanceof ReportMessage) {
                ReportMessage msg = (ReportMessage) message;
                final UUID reporter = msg.getReporter();
                final UUID reportedUser = msg.getReportedUser();
                final String reporterServer = msg.getReporterServer();
                final String reportedUserServer = msg.getReportedUserServer();
                final String reason = msg.getReason();
                String player = plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(reporter);
                String target = plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(reportedUser);
                notifyStaffReport(player, target, reporterServer, reportedUserServer, reason);
            } else {
                throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
            }
        }
    }
}
