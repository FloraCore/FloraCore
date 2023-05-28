package org.floracore.api.bungee.messenger.message.type;

import org.floracore.api.messenger.message.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * 聊天消息
 */
public interface ChatMessage extends Message {
    /**
     * 获取接收者的UUID
     *
     * @return 接收者的UUID
     */
    @NotNull UUID getReceiver();

    /**
     * 获取聊天类型
     *
     * @return 聊天类型
     */
    @NotNull ChatMessageType getType();

    /**
     * 获取参数
     *
     * @return 参数
     */
    @NotNull List<String> getParameters();

    /**
     * 通知类型
     */
    enum ChatMessageType {
        /**
         * 派对消息
         */
        PARTY,
        /**
         * 博主消息
         */
        BLOGGER,
        /**
         * 建筑组消息
         */
        BUILDER,
        /**
         * 员工消息
         */
        STAFF,
        /**
         * 管理员消息
         */
        ADMIN,
    }
}
