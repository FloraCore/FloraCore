package team.floracore.common.messaging;

import org.floracore.api.messenger.*;
import team.floracore.common.util.*;

import java.util.*;

public interface InternalMessagingService {
    void pushChangeName(UUID changer, String name);

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
