package team.floracore.api.bukkit.messenger.message.type;

import org.jetbrains.annotations.NotNull;
import team.floracore.api.messenger.message.Message;

import java.util.UUID;

/**
 * 跨服传送消息
 */
public interface TeleportMessage extends Message {
    /**
     * 获取传送者的UUID
     *
     * @return 传送者的UUID
     */
    @NotNull UUID getSender();

    /**
     * 获取被传送者的UUID
     *
     * @return 被传送者的UUID
     */
    @NotNull UUID getRecipient();

    /**
     * 获取传送的服务器名
     *
     * @return 传送的服务器名
     */
    @NotNull String getServerName();
}
