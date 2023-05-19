package org.floracore.api.commands.report;

import org.floracore.api.data.chat.*;

import java.util.*;

public class ReportDataChatRecord {
    private final UUID uuid;
    private final ChatType chatType;
    private final DataChatRecord dataChatRecord;

    public ReportDataChatRecord(UUID uuid, ChatType chatType, DataChatRecord dataChatRecord) {
        this.uuid = uuid;
        this.chatType = chatType;
        this.dataChatRecord = dataChatRecord;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DataChatRecord getDataChatRecord() {
        return dataChatRecord;
    }
}
