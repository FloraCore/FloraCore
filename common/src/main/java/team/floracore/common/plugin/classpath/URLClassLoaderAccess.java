package team.floracore.common.plugin.classpath;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Provides access to {@link java.net.URLClassLoader}#addURL.
 */
public abstract class URLClassLoaderAccess {

    private final URLClassLoader classLoader;

    protected URLClassLoaderAccess(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Creates a {@link URLClassLoaderAccess} for the given class loader.
     *
     * @param classLoader the class loader
     * @return the access object
     */
    public static URLClassLoaderAccess create(URLClassLoader classLoader) {
        if (Reflection.isSupported()) {
            return new Reflection(classLoader);
        } else {
            return Noop.INSTANCE;
        }
    }

    private static void throwError(Throwable cause) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("FloraCore is unable to inject into the plugin URLClassLoader.\n" +
                "You may be able to fix this problem by adding the following command-line argument " +
                "directly after the 'java' command in your start script: \n'--add-opens java.base/java" +
                ".lang=ALL-UNNAMED'",
                cause);
    }

    /**
     * Adds the given URL to the class loader.
     *
     * @param url the URL to add
     */
    public abstract void addURL(@NotNull URL url);

    /**
     * Accesses using reflection, not supported on Java 9+.
     */
    private static class Reflection extends URLClassLoaderAccess {
        private static final Method ADD_URL_METHOD;

        static {
            Method addUrlMethod;
            try {
                addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
            } catch (Exception e) {
                addUrlMethod = null;
            }
            ADD_URL_METHOD = addUrlMethod;
        }

        Reflection(URLClassLoader classLoader) {
            super(classLoader);
        }

        private static boolean isSupported() {
            return ADD_URL_METHOD != null;
        }

        @Override
        public void addURL(@NotNull URL url) {
            try {
                ADD_URL_METHOD.invoke(super.classLoader, url);
            } catch (ReflectiveOperationException e) {
                URLClassLoaderAccess.throwError(e);
            }
        }
    }

    private static class Noop extends URLClassLoaderAccess {
        private static final Noop INSTANCE = new Noop();

        private Noop() {
            super(null);
        }

        @Override
        public void addURL(@NotNull URL url) {
            URLClassLoaderAccess.throwError(null);
        }
    }

}
