package org.floracore.api.model.online;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Online {
	private final UUID uuid;
	private final boolean online;
	private final String serverName;

	public Online(UUID uuid, boolean online, String serverName) {
		this.uuid = uuid;
		this.online = online;
		this.serverName = serverName;
	}
}
