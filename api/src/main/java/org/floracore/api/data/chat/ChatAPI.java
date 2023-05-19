package org.floracore.api.data.chat;

import java.util.*;

/**
 * 聊天API。
 */
public interface ChatAPI {
    /**
     * 获取玩家在当前服务器的聊天记录UUID。
     *
     * @param uuid 玩家的UUID
     * @return 当前服务器的聊天记录的UUID。
     */
    UUID getPlayerChatUUID(UUID uuid);

    List<DataChatRecord> getPlayerChatUUIDRecent(UUID uuid, int number);

    List<DataChatRecord> getPlayerChatRecentParty(UUID uuid, int number);
}
