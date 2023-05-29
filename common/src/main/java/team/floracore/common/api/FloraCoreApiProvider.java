package team.floracore.common.api;

import org.floracore.api.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.floracore.api.messenger.*;
import org.floracore.api.platform.*;
import org.floracore.api.player.*;
import org.floracore.api.server.*;
import org.floracore.api.translation.*;
import org.jetbrains.annotations.*;
import team.floracore.common.api.implementation.*;
import team.floracore.common.config.*;
import team.floracore.common.messaging.*;
import team.floracore.common.plugin.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.plugin.logging.*;

/**
 * Implements the FloraCore API using the plugin instance
 */
public class FloraCoreApiProvider implements FloraCore {
    private final FloraCorePlugin plugin;

    private final ApiData dataAPI;

    private final ApiPlayer playerAPI;
    private final ApiChat chatAPI;
    private final ApiPlatform platform;
    private final ApiServer server;
    private final ApiTranslation translation;

    public FloraCoreApiProvider(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.dataAPI = new ApiData(plugin);
        this.playerAPI = new ApiPlayer(plugin);
        this.chatAPI = new ApiChat(plugin);
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
                logger.warn("It seems that the FloraCore API has been (class)loaded by a plugin other than FloraCore!");
                logger.warn("The API was loaded by " + apiClassLoader + " (" + guilty + ") and the " + "FloraCore plugin was loaded by " + pluginClassLoader.toString() + ".");
                logger.warn("This indicates that the other plugin has incorrectly \"shaded\" the " + "FloraCore API into its jar file. This can cause errors at runtime and should be fixed.");
                return;
            }
        }
    }

    @Override
    public String getServerName() {
        return plugin.getServerName();
    }

    @Override
    public DataAPI getDataAPI() {
        return this.dataAPI;
    }

    @Override
    public PlayerAPI getPlayerAPI() {
        return this.playerAPI;
    }

    @Override
    public ChatAPI getChatAPI() {
        return this.chatAPI;
    }

    @Override
    public ServerAPI getServerAPI() {
        return this.server;
    }

    @Override
    public TranslationAPI getTranslationAPI() {
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
}
