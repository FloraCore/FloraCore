package org.floracore.api.data.chat;

import java.util.*;

/**
 * 关于服务器聊天记录，通常使用List存储。
 */
public class ChatRecord {
    protected final int id;
    protected final UUID uuid;
    protected final String message;
    protected final long time;

    public ChatRecord(int id, UUID uuid, String message, long time) {
        this.id = id;
        this.uuid = uuid;
        this.message = message;
        this.time = time;
    }

    public int getId() {
        return id;
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
