package team.floracore.common.messaging;

import org.floracore.api.messenger.*;
import org.floracore.api.messenger.message.type.*;

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

    void pushReport(UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer, String reason);

    void pushTeleport(UUID sender, UUID recipient, String serverName);

    void pushConnectServer(UUID recipient, String serverName);

    void pushNoticeMessage(UUID receiver, NoticeMessage.NoticeType type, List<String> parameters);

    void pushChatMessage(UUID receiver, ChatMessage.ChatMessageType type, List<String> parameters);
}
