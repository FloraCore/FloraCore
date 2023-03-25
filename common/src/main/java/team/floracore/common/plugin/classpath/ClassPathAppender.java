package team.floracore.common.plugin.classpath;

import java.nio.file.*;

/**
 * Interface which allows access to add URLs to the plugin classpath at runtime.
 */
public interface ClassPathAppender extends AutoCloseable {

    void addJarToClasspath(Path file);

    @Override
    default void close() {

    }
}
