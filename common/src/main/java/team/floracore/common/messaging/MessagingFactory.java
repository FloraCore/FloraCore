package team.floracore.common.messaging;

import org.floracore.api.messenger.IncomingMessageConsumer;
import org.floracore.api.messenger.Messenger;
import org.floracore.api.messenger.MessengerProvider;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.config.ConfigKeys;
import team.floracore.common.config.FloraCoreConfiguration;
import team.floracore.common.messaging.redis.RedisMessenger;
import team.floracore.common.messaging.sql.SqlMessenger;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.implementation.sql.SqlStorage;
import team.floracore.common.storage.implementation.sql.connection.hikari.MariaDbConnectionFactory;
import team.floracore.common.storage.implementation.sql.connection.hikari.MySqlConnectionFactory;
import team.floracore.common.storage.implementation.sql.connection.hikari.PostgreConnectionFactory;

import java.util.Locale;

public class MessagingFactory<P extends FloraCorePlugin> {
	private final P plugin;

	public MessagingFactory(P plugin) {
		this.plugin = plugin;
	}

	public final InternalMessagingService getInstance() {
		String messagingType = this.plugin.getConfiguration().get(ConfigKeys.MESSAGING_SERVICE);
		if (messagingType.equals("none")) {
			messagingType = "auto";
		}

		// attempt to detect "auto" messaging service type.
		if (messagingType.equals("auto")) {
			if (this.plugin.getConfiguration().get(ConfigKeys.REDIS_ENABLED)) {
				messagingType = "redis";
			} else {
				for (StorageImplementation implementation : this.plugin.getStorage().getImplementations()) {
					if (implementation instanceof SqlStorage) {
						SqlStorage sql = (SqlStorage) implementation;
						if (sql.getConnectionFactory() instanceof MySqlConnectionFactory || sql.getConnectionFactory() instanceof MariaDbConnectionFactory) {
							messagingType = "sql";
							break;
						}
						if (sql.getConnectionFactory() instanceof PostgreConnectionFactory) {
							messagingType = "postgresql";
							break;
						}
					}
				}
			}
		}

		if (messagingType.equals("auto") || messagingType.equals("notsql")) {
			return null;
		}

		if (messagingType.equals("custom")) {
			this.plugin.getLogger()
					.info("Messaging service is set to custom. No service is initialized at this stage yet.");
			return null;
		}

		this.plugin.getLogger().info("Loading messaging service... [" + messagingType.toUpperCase(Locale.ROOT) + "]");
		InternalMessagingService service = getServiceFor(messagingType);
		if (service != null) {
			return service;
		}
		this.plugin.getLogger().warn("Messaging service '" + messagingType + "' not recognised.");
		return null;
	}

	protected InternalMessagingService getServiceFor(String messagingType) {
		if (messagingType.equals("redis")) {
			if (this.plugin.getConfiguration().get(ConfigKeys.REDIS_ENABLED)) {
				try {
					return new FloraCoreMessagingService(this.plugin, new RedisMessengerProvider());
				} catch (Exception e) {
					getPlugin().getLogger().severe("Exception occurred whilst enabling Redis messaging service", e);
				}
			} else {
				this.plugin.getLogger().warn("Messaging Service was set to redis, but redis is not enabled!");
			}
		} else if (messagingType.equals("sql")) {
			try {
				return new FloraCoreMessagingService(this.plugin, new SqlMessengerProvider());
			} catch (Exception e) {
				getPlugin().getLogger().severe("Exception occurred whilst enabling SQL messaging service", e);
			}
		}
		return null;
	}

	protected P getPlugin() {
		return this.plugin;
	}

	private class RedisMessengerProvider implements MessengerProvider {

		@Override
		public @NotNull String getName() {
			return "Redis";
		}

		@Override
		public @NotNull Messenger obtain(@NotNull IncomingMessageConsumer incomingMessageConsumer) {
			RedisMessenger redis = new RedisMessenger(getPlugin(), incomingMessageConsumer);

			FloraCoreConfiguration config = getPlugin().getConfiguration();
			String address = config.get(ConfigKeys.REDIS_ADDRESS);
			String username = config.get(ConfigKeys.REDIS_USERNAME);
			String password = config.get(ConfigKeys.REDIS_PASSWORD);
			if (password.isEmpty()) {
				password = null;
			}
			if (username.isEmpty()) {
				username = null;
			}
			boolean ssl = config.get(ConfigKeys.REDIS_SSL);

			redis.init(address, username, password, ssl);
			return redis;
		}
	}

	private class SqlMessengerProvider implements MessengerProvider {

		@Override
		public @NotNull String getName() {
			return "Sql";
		}

		@Override
		public @NotNull Messenger obtain(@NotNull IncomingMessageConsumer incomingMessageConsumer) {
			for (StorageImplementation implementation : getPlugin().getStorage().getImplementations()) {
				if (implementation instanceof SqlStorage) {
					SqlStorage storage = (SqlStorage) implementation;
					if (storage.getConnectionFactory() instanceof MySqlConnectionFactory || storage.getConnectionFactory() instanceof MariaDbConnectionFactory) {
						// found an implementation match!
						SqlMessenger sql = new SqlMessenger(getPlugin(), storage, incomingMessageConsumer);
						sql.init();
						return sql;
					}
				}
			}

			throw new IllegalStateException("Can't find a supported sql storage implementation");
		}
	}
}
