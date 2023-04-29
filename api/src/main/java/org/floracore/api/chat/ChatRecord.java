package org.floracore.api.chat;

import java.util.*;

public class ChatRecord {
    protected final UUID uuid;
    protected final String message;
    protected final long time;

    public ChatRecord(UUID uuid, String message, long time) {
        this.uuid = uuid;
        this.message = message;
        this.time = time;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
