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

    /**
     * 获取聊天记录的编号
     * 该编号为当前记录的第几号聊天记录
     *
     * @return 聊天记录的编号
     */
    public int getId() {
        return id;
    }

    /**
     * 获取发送这条消息的玩家
     *
     * @return 发送这条消息的玩家
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * 获取发送这条消息的时间
     *
     * @return 发送这条消息的时间
     */
    public long getTime() {
        return time;
    }

    /**
     * 获取发送的消息
     *
     * @return 发送的消息
     */
    public String getMessage() {
        return message;
    }
}
