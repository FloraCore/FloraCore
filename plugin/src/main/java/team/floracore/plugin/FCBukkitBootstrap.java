package team.floracore.plugin;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.loader.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.plugin.classpath.*;
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
public class FCBukkitBootstrap implements FloraCoreBootstrap, LoaderBootstrap, BootstrappedWithLoader {
    private final JavaPlugin loader;

    /**
     * The plugin logger
     */
    private final PluginLogger logger;

    /**
     * A scheduler adapter for the platform
     */
    private final BukkitSchedulerAdapter schedulerAdapter;
    /**
     * A null-safe console instance which delegates to the server logger
     * if {@link Server#getConsoleSender()} returns null.
     */
    private final ConsoleCommandSender console;

    /**
     * The plugin class path appender
     */
    private final ClassPathAppender classPathAppender;

    /**
     * The plugin instance
     */
    private final FCBukkitPlugin plugin;
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

    public FCBukkitBootstrap(JavaPlugin loader) {
        this.loader = loader;

        this.logger = new JavaPluginLogger(loader.getLogger());
        this.schedulerAdapter = new BukkitSchedulerAdapter(this);
        this.classPathAppender = new JarInJarClassPathAppender(getClass().getClassLoader());
        this.console = getServer().getConsoleSender();
        this.plugin = new FCBukkitPlugin(this);
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
    public JavaPlugin getLoader() {
        return this.loader;
    }

    public Server getServer() {
        return this.loader.getServer();
    }

    @Override
    public PluginLogger getPluginLogger() {
        return this.logger;
    }

    @Override
    public BukkitSchedulerAdapter getScheduler() {
        return this.schedulerAdapter;
    }

    // lifecycle

    @Override
    public ClassPathAppender getClassPathAppender() {
        return this.classPathAppender;
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
            getServer().getPluginManager().disablePlugin(this.loader);
            return;
        }

        this.serverStarting = true;
        this.serverStopping = false;
        this.startTime = Instant.now();
        try {
            this.plugin.onEnable();

            // schedule a task to update the 'serverStarting' flag
            getServer().getScheduler().runTask(this.loader, () -> this.serverStarting = false);
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

    @Override
    public CountDownLatch getEnableLatch() {
        return this.enableLatch;
    }

    @Override
    public CountDownLatch getLoadLatch() {
        return this.loadLatch;
    }

    public boolean isServerStarting() {
        return this.serverStarting;
    }

    // provide information about the plugin

    public boolean isServerStopping() {
        return this.serverStopping;
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
    public Path getDataDirectory() {
        return this.loader.getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        Player player = getServer().getPlayer(uniqueId);
        return player != null && player.isOnline();
    }

    public ConsoleCommandSender getConsole() {
        return this.console;
    }

    @Override
    public @Nullable String identifyClassLoader(ClassLoader classLoader) throws ReflectiveOperationException {
        Class<?> pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
        if (pluginClassLoaderClass.isInstance(classLoader)) {
            Method getPluginMethod = pluginClassLoaderClass.getDeclaredMethod("getPlugin");
            getPluginMethod.setAccessible(true);

            JavaPlugin plugin = (JavaPlugin) getPluginMethod.invoke(classLoader);
            return plugin.getName();
        }
        return null;
    }
}
