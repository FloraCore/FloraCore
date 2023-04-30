package org.floracore.api.chat;

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
