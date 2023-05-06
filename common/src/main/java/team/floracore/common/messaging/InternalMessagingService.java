package team.floracore.common.messaging;

import org.floracore.api.messenger.*;

import java.util.*;

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

    void pushReport(UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer);
}
