package team.floracore.common.storage.implementation.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.StorageCredentials;

public abstract class MongoStorage implements StorageImplementation {
	private final FloraCorePlugin plugin;

	private final StorageCredentials configuration;
	private final String prefix;
	private final String connectionUri;
	private MongoClient mongoClient;
	private MongoDatabase database;

	public MongoStorage(FloraCorePlugin plugin, StorageCredentials configuration, String prefix, String connectionUri) {
		this.plugin = plugin;
		this.configuration = configuration;
		this.prefix = prefix;
		this.connectionUri = connectionUri;
	}

	@Override
	public FloraCorePlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public String getImplementationName() {
		return "MongoDB";
	}
}
