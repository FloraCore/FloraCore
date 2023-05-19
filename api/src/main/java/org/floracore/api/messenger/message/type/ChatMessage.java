package org.floracore.api.messenger.message.type;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ChatMessage extends Message {
    /**
     * 获取接收者的UUID
     *
     * @return 接收者的UUID
     */
    @NonNull UUID getReceiver();

    /**
     * 获取聊天类型
     *
     * @return 聊天类型
     */
    @NonNull ChatMessageType getType();

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
        PARTY,
        BLOGGER,
        BUILDER,
        STAFF,
        ADMIN,
    }
}
