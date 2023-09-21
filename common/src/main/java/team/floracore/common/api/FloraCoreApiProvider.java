package team.floracore.common.api;

import org.floracore.api.FloraCore;
import org.floracore.api.FloraCoreProvider;
import org.floracore.api.messenger.MessengerProvider;
import org.floracore.api.model.data.DataManager;
import org.floracore.api.model.data.chat.ChatManager;
import org.floracore.api.model.player.PlayerManager;
import org.floracore.api.platform.PluginMetadata;
import org.floracore.api.server.ServerManager;
import org.floracore.api.translation.TranslationManager;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.api.implementation.ApiChat;
import team.floracore.common.api.implementation.ApiData;
import team.floracore.common.api.implementation.ApiPlatform;
import team.floracore.common.api.implementation.ApiPlayer;
import team.floracore.common.api.implementation.ApiServer;
import team.floracore.common.api.implementation.ApiTranslation;
import team.floracore.common.config.impl.config.ConfigKeys;
import team.floracore.common.messaging.FloraCoreMessagingService;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.plugin.bootstrap.BootstrappedWithLoader;
import team.floracore.common.plugin.bootstrap.FloraCoreBootstrap;
import team.floracore.common.plugin.logging.PluginLogger;

/**
 * Implements the FloraCore API using the plugin instance
 */
public class FloraCoreApiProvider implements FloraCore {
	private final FloraCorePlugin plugin;

	private final ApiData dataManager;

	private final ApiPlayer playerManager;
	private final ApiChat chatManager;
	private final ApiPlatform platform;
	private final ApiServer server;
	private final ApiTranslation translation;

	public FloraCoreApiProvider(FloraCorePlugin plugin) {
		this.plugin = plugin;
		this.dataManager = new ApiData(plugin);
		this.playerManager = new ApiPlayer(plugin);
		this.chatManager = new ApiChat(plugin);
		this.platform = new ApiPlatform(plugin);
		this.server = new ApiServer(plugin);
		this.translation = new ApiTranslation(plugin);
	}

	public void ensureApiWasLoadedByPlugin() {
		FloraCoreBootstrap bootstrap = this.plugin.getBootstrap();
		ClassLoader pluginClassLoader;
		if (bootstrap instanceof BootstrappedWithLoader) {
			pluginClassLoader = ((BootstrappedWithLoader) bootstrap).getLoader().getClass().getClassLoader();
		} else {
			pluginClassLoader = bootstrap.getClass().getClassLoader();
		}

		for (Class<?> apiClass : new Class[]{FloraCore.class, FloraCoreProvider.class}) {
			ClassLoader apiClassLoader = apiClass.getClassLoader();

			if (!apiClassLoader.equals(pluginClassLoader)) {
				String guilty = "unknown";
				try {
					guilty = bootstrap.identifyClassLoader(apiClassLoader);
				} catch (Exception e) {
					// ignore
				}

				PluginLogger logger = this.plugin.getLogger();
				logger.warn("It seems that the FloraCore API has been (class)loaded by a plugin other than " +
						"FloraCore!");
				logger.warn("The API was loaded by " + apiClassLoader + " (" + guilty + ") and the " + "FloraCore " +
						"plugin was loaded by " + pluginClassLoader.toString() + ".");
				logger.warn("This indicates that the other plugin has incorrectly \"shaded\" the " + "FloraCore API " +
						"into its jar file. This can cause errors at runtime and should be fixed.");
				return;
			}
		}
	}

	@Override
	public String getServerName() {
		return plugin.getServerName();
	}

	@Override
	public DataManager getDataManager() {
		return this.dataManager;
	}

	@Override
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	@Override
	public ChatManager getChatManager() {
		return this.chatManager;
	}

	@Override
	public ServerManager getServerManager() {
		return this.server;
	}

	@Override
	public TranslationManager getTranslationManager() {
		return translation;
	}

	@NotNull
	@Override
	public ApiPlatform getPlatform() {
		return platform;
	}

	@Override
	public @NotNull PluginMetadata getPluginMetadata() {
		return this.platform;
	}

	@Override
	public void registerMessengerProvider(@NotNull MessengerProvider messengerProvider) {
		if (this.plugin.getConfiguration().get(ConfigKeys.MESSAGING_SERVICE).equals("custom")) {
			this.plugin.setMessagingService(new FloraCoreMessagingService(this.plugin, messengerProvider));
		}
	}

	@Override
	public Object getFloraCorePlugin() {
		return this.plugin;
	}
}
