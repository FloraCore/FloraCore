package team.floracore.bungee;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.plugin.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.platform.*;
import team.floracore.common.loader.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.plugin.classpath.*;
import team.floracore.common.plugin.logging.PluginLogger;
import team.floracore.common.plugin.logging.*;

import java.lang.reflect.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

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
        this.classPathAppender = new JarInJarClassPathAppender(getClass().getClassLoader());
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
        return this.loader.getDescription().getVersion();
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
        return this.loader.getDataFolder().toPath().toAbsolutePath();
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
        } finally {
            this.loadLatch.countDown();
        }
    }

    @Override
    public void onEnable() {
        if (this.incompatibleVersion) {
            Logger logger = this.loader.getLogger();
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
