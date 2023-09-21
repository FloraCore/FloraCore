package org.floracore.api;

import org.floracore.api.messenger.MessengerProvider;
import org.floracore.api.model.data.DataManager;
import org.floracore.api.model.data.chat.ChatManager;
import org.floracore.api.model.player.PlayerManager;
import org.floracore.api.platform.Platform;
import org.floracore.api.platform.PluginMetadata;
import org.floracore.api.server.ServerManager;
import org.floracore.api.translation.TranslationManager;
import org.jetbrains.annotations.NotNull;

/**
 * The FloraCore API.
 *
 * <p>The API allows other plugins on the server to read and modify FloraCore
 * data, change behaviour of the plugin, listen to certain events, and integrate
 * FloraCore into other plugins and systems.</p>
 *
 * <p>This interface represents the base of the API package. All functions are
 * accessed via this interface.</p>
 *
 * <p>To start using the API, you need to obtain an instance of this interface.
 * These are registered by the FloraCore plugin to the platforms Services
 * Manager. This is the preferred method for obtaining an instance.</p>
 *
 * <p>For ease of use, and for platforms without a Service Manager, an instance
 * can also be obtained from the static singleton accessor in
 * {@link FloraCoreProvider}.</p>
 */
public interface FloraCore {

	/**
	 * Gets the name of this server.
	 *
	 * <p>This is defined in the FloraCore configuration file, and is used for
	 * server specific permission handling.</p>
	 *
	 * <p>The default server name is "global".</p>
	 *
	 * @return the server name
	 */
	String getServerName();

	/**
	 * 获取数据API
	 *
	 * @return 数据API
	 */
	DataManager getDataManager();

	/**
	 * 获取玩家API
	 *
	 * @return 玩家API
	 */
	PlayerManager getPlayerManager();

	/**
	 * 获取聊天API
	 *
	 * @return 聊天API
	 */
	ChatManager getChatManager();

	/**
	 * 获取服务器API
	 *
	 * @return 服务器API
	 */
	ServerManager getServerManager();

	/**
	 * 获取国际化多语言API
	 *
	 * @return 国际化多语言API
	 */
	TranslationManager getTranslationManager();

	/**
	 * Gets the {@link Platform}, which represents the server platform the
	 * plugin is running on.
	 *
	 * @return the platform
	 */
	@NotNull
	Platform getPlatform();

	/**
	 * Gets the {@link PluginMetadata}, responsible for providing metadata about
	 * the FloraCore plugin currently running.
	 *
	 * @return the plugin metadata
	 */
	@NotNull
	PluginMetadata getPluginMetadata();

	/**
	 * Registers a {@link MessengerProvider} for use by the platform.
	 *
	 * <p>Note that the mere action of registering a provider doesn't
	 * necessarily mean that it will be used.</p>
	 *
	 * @param messengerProvider the messenger provider.
	 */
	void registerMessengerProvider(@NotNull MessengerProvider messengerProvider);

	Object getFloraCorePlugin();
}
