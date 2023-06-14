package team.floracore.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.bstats.bungeecord.Metrics;
import org.checkerframework.checker.nullness.qual.Nullable;
import team.floracore.api.platform.Platform;
import team.floracore.common.loader.LoaderBootstrap;
import team.floracore.common.plugin.bootstrap.BootstrappedWithLoader;
import team.floracore.common.plugin.bootstrap.FloraCoreBootstrap;
import team.floracore.common.plugin.classpath.ClassPathAppender;
import team.floracore.common.plugin.classpath.ReflectionClassPathAppender;
import team.floracore.common.plugin.logging.JavaPluginLogger;
import team.floracore.common.plugin.logging.PluginLogger;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Bootstrap plugin for FloraCore running on Bukkit.
 */
public class FCBungeeBootstrap implements FloraCoreBootstrap, LoaderBootstrap, BootstrappedWithLoader {
    public static Plugin loader;

    /**
     * The plugin logger
     */
    private final PluginLogger logger;

    /**
     * A scheduler adapter for the platform
     */
    private final BungeeSchedulerAdapter schedulerAdapter;

    /**
     * The plugin class path appender
     */
    private final ClassPathAppender classPathAppender;

    /**
     * The plugin instance
     */
    private final FCBungeePlugin plugin;
    // load/enable latches
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch enableLatch = new CountDownLatch(1);
    /**
     * The time when the plugin was enabled
     */
    private Instant startTime;
    private boolean serverStarting = true;
    private boolean serverStopping = false;

    // if the plugin has been loaded on an incompatible version
    private boolean incompatibleVersion = false;

    public FCBungeeBootstrap(Plugin loader) {
        FCBungeeBootstrap.loader = loader;

        this.logger = new JavaPluginLogger(loader.getLogger());
        this.schedulerAdapter = new BungeeSchedulerAdapter(this);
        this.classPathAppender = new ReflectionClassPathAppender(getClass().getClassLoader());
        this.plugin = new FCBungeePlugin(this);
    }

    // provide adapters

    private static boolean checkIncompatibleVersion() {
        try {
            Class.forName("com.google.gson.JsonElement");
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    @Override
    public Plugin getLoader() {
        return loader;
    }

    @Override
    public PluginLogger getPluginLogger() {
        return this.logger;
    }

    @Override
    public BungeeSchedulerAdapter getScheduler() {
        return this.schedulerAdapter;
    }

    @Override
    public ClassPathAppender getClassPathAppender() {
        return this.classPathAppender;
    }

    // lifecycle

    @Override
    public CountDownLatch getLoadLatch() {
        return this.loadLatch;
    }

    @Override
    public CountDownLatch getEnableLatch() {
        return this.enableLatch;
    }

    @Override
    public String getVersion() {
        return loader.getDescription().getVersion();
    }

    @Override
    public Instant getStartupTime() {
        return this.startTime;
    }

    @Override
    public Platform.Type getType() {
        return Platform.Type.BUNGEECORD;
    }

    @Override
    public Path getDataDirectory() {
        return loader.getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        return getProxy().getPlayer(uniqueId) != null;
    }

    // provide information about the plugin

    @Override
    public @Nullable String identifyClassLoader(ClassLoader classLoader) throws Exception {
        Class<?> pluginClassLoader = Class.forName("net.md_5.bungee.api.plugin.PluginClassloader");
        if (pluginClassLoader.isInstance(classLoader)) {
            Field descriptionField = pluginClassLoader.getDeclaredField("desc");
            descriptionField.setAccessible(true);

            PluginDescription desc = (PluginDescription) descriptionField.get(classLoader);
            return desc.getName();
        }
        return null;
    }

    public ProxyServer getProxy() {
        return loader.getProxy();
    }

    @Override
    public void onLoad() {
        if (checkIncompatibleVersion()) {
            this.incompatibleVersion = true;
            return;
        }
        try {
            this.plugin.onLoad();
            // Since 2.0.5.2
            new Metrics(this.getLoader(), 18688);
        } finally {
            this.loadLatch.countDown();
        }
    }

    @Override
    public void onEnable() {
        if (this.incompatibleVersion) {
            Logger logger = loader.getLogger();
            logger.severe("----------------------------------------------------------------------");
            logger.severe("Your server version is not compatible with this build of FloraCore. :(");
            logger.severe("----------------------------------------------------------------------");
            getProxy().stop();
            return;
        }

        this.serverStarting = true;
        this.serverStopping = false;
        this.startTime = Instant.now();
        try {
            this.plugin.onEnable();
        } finally {
            this.enableLatch.countDown();
        }
    }

    @Override
    public void onDisable() {
        if (this.incompatibleVersion) {
            return;
        }

        this.serverStopping = true;
        this.plugin.onDisable();
    }

    public boolean isServerStarting() {
        return this.serverStarting;
    }

    public boolean isServerStopping() {
        return this.serverStopping;
    }
}
