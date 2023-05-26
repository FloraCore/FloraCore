package org.floracore.api;

import org.jetbrains.annotations.*;

import static org.jetbrains.annotations.ApiStatus.*;

/**
 * Provides static access to the {@link FloraCore} API.
 *
 * <p>Ideally, the ServiceManager for the platform should be used to obtain an
 * instance, however, this provider can be used if this is not viable.</p>
 */
public final class FloraCoreProvider {
    private static FloraCore instance = null;

    @Internal
    private FloraCoreProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Gets an instance of the {@link FloraCore} API,
     * throwing {@link IllegalStateException} if the API is not loaded yet.
     *
     * <p>This method will never return null.</p>
     *
     * @return an instance of the FloraCore API
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static @NotNull FloraCore get() {
        FloraCore instance = FloraCoreProvider.instance;
        if (instance == null) {
            throw new NotLoadedException();
        }
        return instance;
    }

    @Internal
    static void register(FloraCore instance) {
        FloraCoreProvider.instance = instance;
    }

    @Internal
    static void unregister() {
        FloraCoreProvider.instance = null;
    }

    /**
     * Exception thrown when the API is requested before it has been loaded.
     */
    private static final class NotLoadedException extends IllegalStateException {
        private static final String MESSAGE = "The FloraCore API isn't loaded yet!\n" +
                "This could be because:\n" +
                "  a) the FloraCore plugin is not installed or it failed to enable\n" +
                "  b) the plugin in the stacktrace does not declare a dependency on FloraCore\n" +
                "  c) the plugin in the stacktrace is retrieving the API before the plugin 'enable' phase\n" +
                "     (call the #get method in onEnable, not the constructor!)\n";

        NotLoadedException() {
            super(MESSAGE);
        }
    }

}
