package team.floracore.common.messaging;

import org.floracore.api.messenger.Messenger;
import org.floracore.api.messenger.MessengerProvider;
import team.floracore.common.util.ExpiringSet;

import java.util.UUID;

public interface InternalMessagingService {
    /**
     * Gets the name of this messaging service
     *
     * @return the name of this messaging service
     */
    String getName();

    Messenger getMessenger();

    MessengerProvider getMessengerProvider();

    /**
     * Closes the messaging service
     */
    void close();

    UUID generatePingId();

    ExpiringSet<UUID> getReceivedMessages();

    void addReceivedMessage(UUID uuid);
}
