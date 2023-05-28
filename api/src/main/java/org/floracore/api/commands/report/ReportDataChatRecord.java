package org.floracore.api.commands.report;

import org.floracore.api.data.chat.*;

import java.util.*;

/**
 * 举报聊天记录
 */
public class ReportDataChatRecord {
    private final UUID uuid;
    private final ChatType chatType;
    private final DataChatRecord dataChatRecord;

    public ReportDataChatRecord(UUID uuid, ChatType chatType, DataChatRecord dataChatRecord) {
        this.uuid = uuid;
        this.chatType = chatType;
        this.dataChatRecord = dataChatRecord;
    }

    /**
     * 获取聊天类型
     *
     * @return 聊天类型
     */
    public ChatType getChatType() {
        return chatType;
    }

    /**
     * 获取目标玩家的UUID
     *
     * @return 目标玩家的UUID
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * 获取聊天记录
     *
     * @return 聊天记录
     */
    public DataChatRecord getDataChatRecord() {
        return dataChatRecord;
    }
}
