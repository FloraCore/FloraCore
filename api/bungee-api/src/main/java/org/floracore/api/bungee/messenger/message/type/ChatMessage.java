package org.floracore.api.bungee.messenger.message.type;

import org.floracore.api.messenger.message.Message;
import org.floracore.api.model.data.chat.ChatType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

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
	@NotNull ChatType getType();

	/**
	 * 获取参数
	 *
	 * @return 参数
	 */
	@NotNull List<String> getParameters();
}
