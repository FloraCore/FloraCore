package org.floracore.api.data.chat;

import java.util.*;

/**
 * 聊天API。
 */
public interface ChatAPI {
    List<DataChatRecord> getPlayerChatUUIDRecent(UUID uuid, int number);

    List<DataChatRecord> getPlayerChatRecentParty(UUID uuid, int number);
}
