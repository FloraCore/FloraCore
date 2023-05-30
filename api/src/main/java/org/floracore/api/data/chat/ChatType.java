package org.floracore.api.data.chat;

/**
 * 聊天类型
 */
public enum ChatType {
    /**
     * 在服务器内聊天,即表示的是公共聊天。
     */
    SERVER,
    ADMIN,
    BLOGGER,
    BUILDER,
    FRIEND,
    GUILD,
    /**
     * 在组队内的聊天记录,当且仅当玩家有组队时,记录的聊天记录才会是PARTY类型。
     */
    PARTY,
    STAFF,
}
