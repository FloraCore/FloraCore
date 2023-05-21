package team.floracore.common.plugin;

import okhttp3.*;
import team.floracore.common.api.*;
import team.floracore.common.config.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.extension.*;
import team.floracore.common.locale.data.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.messaging.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.plugin.logging.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.*;

import java.util.*;
import java.util.stream.*;

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

    NamesRepository getNamesRepository();

    /**
     * Gets the primary data storage instance. This is likely to be wrapped with extra layers for caching, etc.
     *
     * @return the storage handler instance
     */
    Storage getStorage();

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

    Optional<InternalMessagingService> getMessagingService();

    void setMessagingService(InternalMessagingService messagingService);

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

    Map<String, List<String>> getLoadedPlugins();

    boolean isPluginInstalled(String name);

    boolean isPluginInstalled(String name, String author);

    String getServerName();
}
