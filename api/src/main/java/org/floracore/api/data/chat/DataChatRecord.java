package org.floracore.api.data.chat;

/**
 * 玩家在当前服务器的聊天记录。
 * 包括了加入时间和离开时间，方便定位聊天记录。
 */
public class DataChatRecord {
    protected final int id;
    protected final long joinTime;
    protected final long quitTime;

    public DataChatRecord(int id, long joinTime, long quitTime) {
        this.id = id;
        this.joinTime = joinTime;
        this.quitTime = quitTime;
    }

    /**
     * 获取聊天记录的键值
     *
     * @return 聊天记录的键值
     */
    public int getId() {
        return id;
    }

    /**
     * 获取加入聊天的时间
     *
     * @return 加入聊天的时间
     */
    public long getJoinTime() {
        return joinTime;
    }

    /**
     * 获取退出聊天的时间
     *
     * @return 退出聊天的时间
     */
    public long getQuitTime() {
        return quitTime;
    }
}
