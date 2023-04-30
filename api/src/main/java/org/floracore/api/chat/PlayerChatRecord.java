package org.floracore.api.chat;

/**
 * 玩家在当前服务器的聊天记录。
 * 包括了加入时间和离开时间，方便定位聊天记录。
 */
public class PlayerChatRecord {
    protected final int id;
    protected final long joinTime;
    protected final long quitTime;

    public PlayerChatRecord(int id, long joinTime, long quitTime) {
        this.id = id;
        this.joinTime = joinTime;
        this.quitTime = quitTime;
    }

    public int getId() {
        return id;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public long getQuitTime() {
        return quitTime;
    }
}
