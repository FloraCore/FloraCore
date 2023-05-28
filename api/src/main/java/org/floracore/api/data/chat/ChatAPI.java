package org.floracore.api.data.chat;

import java.util.*;

/**
 * 聊天API。
 */
public interface ChatAPI {
    /**
     * 获取最近的几条聊天记录。
     * 返回的类型为 {@link ChatType#SERVER}
     *
     * @param uuid   目标玩家的UUID
     * @param number 需要的聊天记录数量
     * @return 聊天记录
     */
    List<DataChatRecord> getPlayerChatUUIDRecent(UUID uuid, int number);

    /**
     * 获取最近的几条聊天记录。
     * 返回的类型为 {@link ChatType#PARTY}
     *
     * @param uuid   目标玩家的UUID
     * @param number 需要的聊天记录数量
     * @return 聊天记录
     */

    List<DataChatRecord> getPlayerChatRecentParty(UUID uuid, int number);
}
