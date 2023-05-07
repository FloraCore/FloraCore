package org.floracore.api.messenger.message.type;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.*;

import java.util.*;

public interface TeleportMessage extends Message {
    /**
     * 获取传送者的UUID
     *
     * @return 传送者的UUID
     */
    @NonNull UUID getSender();

    /**
     * 获取被传送者的UUID
     *
     * @return 被传送者的UUID
     */
    @NonNull UUID getRecipient();

    /**
     * 获取传送的服务器名
     *
     * @return 传送的服务器名
     */
    @NonNull String getServerName();
}
