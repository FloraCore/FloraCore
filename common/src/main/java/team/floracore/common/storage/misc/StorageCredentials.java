package team.floracore.common.storage.misc;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;

public class StorageCredentials {

	private final String address;
	private final String database;
	private final String username;
	private final String password;
	@Getter
	private final int maxPoolSize;
	@Getter
	private final int minIdleConnections;
	@Getter
	private final int maxLifetime;
	@Getter
	private final int keepAliveTime;
	@Getter
	private final int connectionTimeout;
	@Getter
	private final Map<String, String> properties;

	public StorageCredentials(String address,
	                          String database,
	                          String username,
	                          String password,
	                          int maxPoolSize,
	                          int minIdleConnections,
	                          int maxLifetime,
	                          int keepAliveTime,
	                          int connectionTimeout,
	                          Map<String, String> properties) {
		this.address = address;
		this.database = database;
		this.username = username;
		this.password = password;
		this.maxPoolSize = maxPoolSize;
		this.minIdleConnections = minIdleConnections;
		this.maxLifetime = maxLifetime;
		this.keepAliveTime = keepAliveTime;
		this.connectionTimeout = connectionTimeout;
		this.properties = properties;
	}

	public String getAddress() {
		return Objects.requireNonNull(this.address, "address");
	}

	public String getDatabase() {
		return Objects.requireNonNull(this.database, "database");
	}

	public String getUsername() {
		return Objects.requireNonNull(this.username, "username");
	}

	public String getPassword() {
		return Objects.requireNonNull(this.password, "password");
	}

}
