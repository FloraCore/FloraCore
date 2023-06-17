package org.floracore.api.messenger.message.type;

import org.floracore.api.messenger.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface KickMessage extends Message {
    /**
     * 获取接收者的UUID
     *
     * @return 接收者的UUID
     */
    @NotNull UUID getReceiver();
    /**
     * 获取原因
     *
     * @return 踢出原因
     */
    @NotNull String getReason();
}
