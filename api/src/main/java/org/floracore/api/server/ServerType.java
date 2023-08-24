package org.floracore.api.server;

import lombok.Getter;

/**
 * 服务器类型
 */
@Getter
public enum ServerType {
	/**
	 * 普通类型
	 */
	NORMAL("normal", false, true),
	/**
	 * 大厅类型
	 */
	LOBBY("lobby", true, false),
	/**
	 * 游戏类型
	 */
	GAME("game", false, true),
	/**
	 * BungeeCord类型
	 */
	BUNGEECORD("bungeecord", false, false),
	/**
	 * 未知的类型
	 */
	UNKNOWN("unknown", true, false);
	final String name;
	final boolean autoSync1;
	final boolean autoSync2;

	ServerType(String name, boolean autoSync1, boolean autoSync2) {
		this.name = name;
		this.autoSync1 = autoSync1;
		this.autoSync2 = autoSync2;
	}

	public static ServerType parse(String name) {
		return parse(name, UNKNOWN);
	}

	public static ServerType parse(String name, ServerType def) {
		for (ServerType t : values()) {
			if (t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return def;
	}

}