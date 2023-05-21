package team.floracore.common.messaging;

import org.floracore.api.messenger.*;

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
}
