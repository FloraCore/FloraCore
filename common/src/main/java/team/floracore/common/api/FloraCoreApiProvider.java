package team.floracore.common.api;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.floracore.api.platform.*;
import org.floracore.api.player.*;
import org.jetbrains.annotations.*;
import team.floracore.common.api.implementation.*;
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

    public FloraCoreApiProvider(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.dataAPI = new ApiData(plugin);
        this.playerAPI = new ApiPlayer(plugin);
        this.chatAPI = new ApiChat(plugin);
        this.platform = new ApiPlatform(plugin);
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

    @NotNull
    @Override
    public ApiPlatform getPlatform() {
        return platform;
    }

    @Override
    public @NonNull PluginMetadata getPluginMetadata() {
        return this.platform;
    }
}
