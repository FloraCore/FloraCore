package team.floracore.common.plugin.bootstrap;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.floracore.api.platform.Platform;
import team.floracore.common.plugin.classpath.ClassPathAppender;
import team.floracore.common.plugin.logging.PluginLogger;
import team.floracore.common.plugin.scheduler.SchedulerAdapter;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

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
	 * Gets the platform type this instance of FloraCore is running on.
	 *
	 * @return the platform type
	 */
	Platform.Type getType();

	/**
	 * Gets the plugins configuration directory
	 *
	 * @return the config directory
	 */
	default Path getConfigDirectory() {
		return getDataDirectory();
	}

	/**
	 * Gets the plugins main data storage directory
	 *
	 * <p>Bukkit: ./plugins/FloraCore</p>
	 *
	 * @return the platforms data folder
	 */
	Path getDataDirectory();

	/**
	 * Checks if a user is online
	 *
	 * @param uniqueId the users external uuid
	 * @return true if the user is online
	 */
	boolean isPlayerOnline(UUID uniqueId);

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
