package team.floracore.common.plugin.bootstrap;

import org.bukkit.*;
import org.bukkit.plugin.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.plugin.classpath.*;
import team.floracore.common.plugin.logging.PluginLogger;
import team.floracore.common.plugin.scheduler.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public interface FloraCoreBootstrap {
    /**
     * Gets the plugin logger
     *
     * @return the logger
     */
    PluginLogger getPluginLogger();

    /**
     * Gets an adapter for the platforms scheduler
     *
     * @return the scheduler
     */
    SchedulerAdapter getScheduler();

    /**
     * Gets a {@link ClassPathAppender} for this bootstrap plugin
     *
     * @return a class path appender
     */
    ClassPathAppender getClassPathAppender();

    /**
     * Returns a countdown latch which {@link CountDownLatch#countDown() counts down}
     * after the plugin has loaded.
     *
     * @return a loading latch
     */
    CountDownLatch getLoadLatch();

    /**
     * Returns a countdown latch which {@link CountDownLatch#countDown() counts down}
     * after the plugin has enabled.
     *
     * @return an enable latch
     */
    CountDownLatch getEnableLatch();

    /**
     * Gets a string of the plugin's version
     *
     * @return the version of the plugin
     */
    String getVersion();

    /**
     * Gets the time when the plugin first started in millis.
     *
     * @return the enable time
     */
    Instant getStartupTime();


    /**
     * Gets the plugins main data storage directory
     *
     * <p>Bukkit: ./plugins/FloraCore</p>
     *
     * @return the platforms data folder
     */
    Path getDataDirectory();

    /**
     * Gets the plugins configuration directory
     *
     * @return the config directory
     */
    default Path getConfigDirectory() {
        return getDataDirectory();
    }


    /**
     * Checks if a user is online
     *
     * @param uniqueId the users external uuid
     * @return true if the user is online
     */
    boolean isPlayerOnline(UUID uniqueId);

    Plugin getPlugin();

    Server getServer();

    /**
     * Gets a bundled resource file from the jar
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    default InputStream getResourceStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    /**
     * Attempts to identify the plugin behind the given classloader.
     *
     * <p>Used for giving more helpful log messages when things break.</p>
     *
     * @param classLoader the classloader to identify
     * @return the name of the classloader source
     * @throws Exception anything
     */
    default @Nullable String identifyClassLoader(ClassLoader classLoader) throws Exception {
        return null;
    }
}
