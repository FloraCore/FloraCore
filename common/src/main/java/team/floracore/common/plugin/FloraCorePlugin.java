package team.floracore.common.plugin;

import com.google.gson.JsonElement;
import okhttp3.OkHttpClient;
import team.floracore.common.api.FloraCoreApiProvider;
import team.floracore.common.config.FloraCoreConfiguration;
import team.floracore.common.dependencies.DependencyManager;
import team.floracore.common.extension.SimpleExtensionManager;
import team.floracore.common.http.BytebinClient;
import team.floracore.common.http.BytesocksClient;
import team.floracore.common.locale.data.DataManager;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.locale.translation.TranslationRepository;
import team.floracore.common.messaging.InternalMessagingService;
import team.floracore.common.plugin.bootstrap.FloraCoreBootstrap;
import team.floracore.common.plugin.logging.PluginLogger;
import team.floracore.common.script.ScriptLoader;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.Storage;
import team.floracore.common.storage.StorageFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface FloraCorePlugin {
	/**
	 * Gets the bootstrap plugin instance
	 *
	 * @return the bootstrap plugin
	 */
	FloraCoreBootstrap getBootstrap();

	OkHttpClient getHttpClient();

	/**
	 * Gets a wrapped logger instance for the platform.
	 *
	 * @return the plugin's logger
	 */
	PluginLogger getLogger();

	/**
	 * Gets the dependency manager for the plugin
	 *
	 * @return the dependency manager
	 */
	DependencyManager getDependencyManager();

	/**
	 * Gets the plugin's configuration
	 *
	 * @return the plugin config
	 */
	FloraCoreConfiguration getConfiguration();

	/**
	 * Gets the primary data storage instance. This is likely to be wrapped with extra layers for caching, etc.
	 *
	 * @return the storage handler instance
	 */
	Storage getStorage();

	void setStorage(Storage storage);

	/**
	 * Gets a list of online Senders on the platform
	 *
	 * @return a {@link java.util.List} of senders online on the platform
	 */
	Stream<Sender> getOnlineSenders();

	/**
	 * Gets the console.
	 *
	 * @return the console sender of the instance
	 */
	Sender getConsoleSender();

	/**
	 * Returns the class implementing the FloraCoreAPI on this platform.
	 *
	 * @return the api
	 */
	FloraCoreApiProvider getApiProvider();

	/**
	 * Gets the extension manager.
	 *
	 * @return the extension manager
	 */
	SimpleExtensionManager getExtensionManager();

	/**
	 * Gets the instance providing locale translations for the plugin
	 *
	 * @return the translation manager
	 */
	TranslationManager getTranslationManager();

	/**
	 * Gets the translation repository
	 *
	 * @return the translation repository
	 */
	TranslationRepository getTranslationRepository();

	DataManager getDataManager();

	String getServerName();

	/**
	 * Gets the messaging service.
	 *
	 * @return the messaging service
	 */
	Optional<InternalMessagingService> getMessagingService();

	/**
	 * Sets the messaging service.
	 *
	 * @param service the service
	 */
	void setMessagingService(InternalMessagingService service);

	boolean processIncomingMessage(String type, JsonElement content, UUID id);

	StorageFactory getStorageFactory();

	boolean luckPermsHook();

	Sender getSender(UUID uuid);

	BytebinClient getBytebin();

	BytesocksClient getBytesocks();

	ScriptLoader getScriptLoader();

	void setScriptLoader(ScriptLoader scriptLoader);
}
