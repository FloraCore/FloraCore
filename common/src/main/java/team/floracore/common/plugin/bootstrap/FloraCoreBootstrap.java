package team.floracore.common.plugin.bootstrap;

import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.plugin.classpath.*;
import team.floracore.common.plugin.logging.*;

import java.time.*;
import java.util.concurrent.*;

public interface FloraCoreBootstrap {
    /**
     * Gets the plugin logger
     *
     * @return the logger
     */
    PluginLogger getPluginLogger();

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
