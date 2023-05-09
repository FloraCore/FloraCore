package org.floracore.api.commands.report;

import org.floracore.api.data.chat.*;

import java.util.*;

public class ReportDataChatRecord {
    private final UUID uuid;
    private final DataChatRecord dataChatRecord;

    public ReportDataChatRecord(UUID uuid, DataChatRecord dataChatRecord) {
        this.uuid = uuid;
        this.dataChatRecord = dataChatRecord;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DataChatRecord getDataChatRecord() {
        return dataChatRecord;
    }
}
