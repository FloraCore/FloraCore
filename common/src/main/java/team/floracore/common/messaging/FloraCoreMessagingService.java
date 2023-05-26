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
        boolean valid = true;

        // instead of throwing an exception here, just return false
        // it means an instance of FC can gracefully handle messages it doesn't
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
            this.plugin.getLogger()
                    .warn("Unable to decode incoming messaging service message: '" + encodedString + "'", e);
            return false;
        }
    }

    private boolean consumeIncomingMessageAsString0(@NonNull String encodedString) {
        Objects.requireNonNull(encodedString, "encodedString");
        JsonObject parsed = Objects.requireNonNull(GsonProvider.normal().fromJson(encodedString, JsonObject.class),
                "parsed");
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
        /*switch (type) {
            case UpdateMessageImpl.TYPE:
                decoded = UpdateMessageImpl.decode(content, id);
                break;
            case UserUpdateMessageImpl.TYPE:
                decoded = UserUpdateMessageImpl.decode(content, id);
                break;
            case ActionLogMessageImpl.TYPE:
                decoded = ActionLogMessageImpl.decode(content, id);
                break;
            default:
                // gracefully return if we just don't recognise the type
                return false;
        }

        // consume the message
        processIncomingMessage(decoded);*/
        return true;
    }

    private void processIncomingMessage(Message message) {
        /*if (message instanceof UpdateMessage) {
            UpdateMessage msg = (UpdateMessage) message;

            this.plugin.getLogger().info("[Messaging] Received update ping with id: " + msg.getId());

            if (this.plugin.getEventDispatcher().dispatchNetworkPreSync(false, msg.getId())) {
                return;
            }

            this.plugin.getSyncTaskBuffer().request();
        } else if (message instanceof UserUpdateMessage) {
            UserUpdateMessage msg = (UserUpdateMessage) message;

            User user = this.plugin.getUserManager().getIfLoaded(msg.getUserUniqueId());
            if (user == null) {
                return;
            }

            this.plugin.getLogger().info("[Messaging] Received user update ping for '" + user.getPlainDisplayName() + "' with id: " + msg.getId());

            if (this.plugin.getEventDispatcher().dispatchNetworkPreSync(false, msg.getId())) {
                return;
            }

            this.plugin.getStorage().loadUser(user.getUniqueId(), null);
        } else if (message instanceof ActionLogMessage) {
            ActionLogMessage msg = (ActionLogMessage) message;

            this.plugin.getEventDispatcher().dispatchLogReceive(msg.getId(), msg.getAction());
            this.plugin.getLogDispatcher().dispatchFromRemote((LoggedAction) msg.getAction());
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
        }*/
    }
}
