package team.floracore.bukkit;

import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import team.floracore.api.platform.Platform;
import team.floracore.bukkit.commands.player.EnderChestCommand;
import team.floracore.common.loader.LoaderBootstrap;
import team.floracore.common.plugin.bootstrap.BootstrappedWithLoader;
import team.floracore.common.plugin.bootstrap.FloraCoreBootstrap;
import team.floracore.common.plugin.classpath.ClassPathAppender;
import team.floracore.common.plugin.classpath.ReflectionClassPathAppender;
import team.floracore.common.plugin.logging.JavaPluginLogger;
import team.floracore.common.plugin.logging.PluginLogger;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Bootstrap plugin for FloraCore running on Bukkit.
 */
public class FCBukkitBootstrap implements FloraCoreBootstrap, LoaderBootstrap, BootstrappedWithLoader {
    public static JavaPlugin loader;

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
        FCBukkitBootstrap.loader = loader;

        this.logger = new JavaPluginLogger(loader.getLogger());
        this.schedulerAdapter = new BukkitSchedulerAdapter(this);
        this.classPathAppender = new ReflectionClassPathAppender(getClass().getClassLoader());
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

    public Server getServer() {
        return loader.getServer();
    }

    @Override
    public JavaPlugin getLoader() {
        return loader;
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
        return Platform.Type.BUKKIT;
    }

    @Override
    public Path getDataDirectory() {
        return loader.getDataFolder().toPath().toAbsolutePath();
    }

    // provide information about the plugin

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        Player player = getServer().getPlayer(uniqueId);
        return player != null && player.isOnline();
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

    @Override
    public void onLoad() {
        if (checkIncompatibleVersion()) {
            this.incompatibleVersion = true;
            return;
        }
        try {
            this.plugin.onLoad();
            // Since 2.0.5.2
            new Metrics(this.getLoader(), 18690);
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
            getServer().getPluginManager().disablePlugin(loader);
            return;
        }

        this.serverStarting = true;
        this.serverStopping = false;
        this.startTime = Instant.now();
        try {
            this.plugin.onEnable();

            // schedule a task to update the 'serverStarting' flag
            getServer().getScheduler().runTask(loader, () -> this.serverStarting = false);
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

        EnderChestCommand.READONLY_MAP.clear();
    }

    public boolean isServerStarting() {
        return this.serverStarting;
    }

    public boolean isServerStopping() {
        return this.serverStopping;
    }

    public ConsoleCommandSender getConsole() {
        return this.console;
    }
}
