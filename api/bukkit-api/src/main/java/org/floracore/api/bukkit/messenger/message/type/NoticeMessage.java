package org.floracore.api.bukkit.messenger.message.type;

import org.floracore.api.messenger.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * 通知消息
 */
public interface NoticeMessage extends Message {
	/**
	 * 获取接收者的UUID
	 *
	 * @return 接收者的UUID
	 */
	@NotNull UUID getReceiver();

	/**
	 * 获取通知类型
	 *
	 * @return 通知类型
	 */
	@NotNull NoticeType getType();

	/**
	 * 获取参数
	 *
	 * @return 参数
	 */
	@NotNull List<String> getParameters();

	/**
	 * 通知类型
	 */
	enum NoticeType {
	}
}
