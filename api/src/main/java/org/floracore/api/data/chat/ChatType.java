package org.floracore.api.data.chat;

import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

/**
 * 聊天类型
 */
@Getter
public enum ChatType {
	/**
	 * 在服务器内聊天,即表示的是公共聊天。
	 */
	SERVER("all", "a"),
	/**
	 * 管理员消息
	 */
	ADMIN("admin"),
	/**
	 * 建筑组消息
	 */
	BUILDER("builder"),
	/**
	 * 好友消息
	 */
	FRIEND,
	/**
	 * 公会消息
	 */
	GUILD("guild"),
	/**
	 * 在组队内的聊天记录,当且仅当玩家有组队时,记录的聊天记录才会是PARTY类型。
	 */
	PARTY("party", "p"),
	/**
	 * 员工消息
	 */
	STAFF("staff", "s"),
	/**
	 * 自定义消息
	 */
	CUSTOM;
	private final List<String> identifiers;

	ChatType(String... identifiers) {
		this.identifiers = ImmutableList.copyOf(identifiers);
	}

	public static ChatType parse(String name) {
		for (ChatType t : values()) {
			for (String id : t.getIdentifiers()) {
				if (id.equalsIgnoreCase(name)) {
					return t;
				}
			}
		}
		return null;
	}

}
