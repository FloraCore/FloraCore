package team.floracore.common.extension;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.api.extension.*;
import team.floracore.common.plugin.*;
import team.floracore.common.plugin.classpath.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;

public class SimpleExtensionManager implements ExtensionManager, AutoCloseable {
    private final FloraCorePlugin plugin;
    private final Set<LoadedExtension> extensions = new HashSet<>();

    public SimpleExtensionManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    private static boolean isJarInJar() {
        String thisClassLoaderName = SimpleExtensionManager.class.getClassLoader().getClass().getName();
        return thisClassLoaderName.equals("team.floracore.common.loader.JarInJarClassLoader");
    }

    @Deprecated
    private static void addJarToParentClasspath(Path path) throws Exception {
        ClassLoader parentClassLoader = SimpleExtensionManager.class.getClassLoader().getParent();
        if (!(parentClassLoader instanceof URLClassLoader)) {
            throw new RuntimeException("useParentClassLoader is true but parent is not a URLClassLoader");
        }

        URLClassLoaderAccess.create(((URLClassLoader) parentClassLoader)).addURL(path.toUri().toURL());
    }

    @Override
    public void close() {
        for (LoadedExtension extension : this.extensions) {
            try {
                extension.instance.unload();
            } catch (Exception e) {
                this.plugin.getLogger().warn("Exception unloading extension", e);
            }
        }
        this.extensions.clear();
    }

    @Override
    public void loadExtension(Extension extension) {
        if (this.extensions.stream().anyMatch(e -> e.instance.equals(extension))) {
            return;
        }
        this.plugin.getLogger().info("Loading extension: " + extension.getClass().getName());
        this.extensions.add(new LoadedExtension(extension, null));
        extension.load();
    }

    public void loadExtensions(Path directory) {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return;
        }

        try (Stream<Path> stream = Files.list(directory)) {
            stream.forEach(path -> {
                if (path.getFileName().toString().endsWith(".jar")) {
                    try {
                        loadExtension(path);
                    } catch (Exception e) {
                        this.plugin.getLogger().warn("Exception loading extension from " + path, e);
                    }
                }
            });
        } catch (IOException e) {
            this.plugin.getLogger().warn("Exception loading extensions from " + directory, e);
        }
    }

    @Override
    public @NonNull Extension loadExtension(Path path) throws IOException {
        if (this.extensions.stream().anyMatch(e -> path.equals(e.path))) {
            throw new IllegalStateException("Extension at path " + path + " already loaded.");
        }

        if (!Files.exists(path)) {
            throw new NoSuchFileException("No file at " + path);
        }

        String className;
        boolean useParentClassLoader = false;
        try (JarFile jar = new JarFile(path.toFile())) {
            JarEntry extensionJarEntry = jar.getJarEntry("extension.json");
            if (extensionJarEntry == null) {
                throw new IllegalStateException("extension.json not present");
            }
            try (InputStream in = jar.getInputStream(extensionJarEntry)) {
                if (in == null) {
                    throw new IllegalStateException("extension.json not present");
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    JsonObject parsed = JsonParser.parseReader(reader).getAsJsonObject();
                    className = parsed.get("class").getAsString();
                    if (parsed.has("useParentClassLoader")) {
                        useParentClassLoader = parsed.get("useParentClassLoader").getAsBoolean();
                    }
                }
            }
        }

        if (className == null) {
            throw new IllegalArgumentException("class is null");
        }

        if (useParentClassLoader && isJarInJar()) {
            try {
                addJarToParentClasspath(path);
            } catch (Throwable e) {
                throw new RuntimeException("Exception whilst classloading extension", e);
            }
        } else {
            this.plugin.getBootstrap().getClassPathAppender().addJarToClasspath(path);
        }

        Class<? extends Extension> extensionClass;
        try {
            extensionClass = Class.forName(className).asSubclass(Extension.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.plugin.getLogger().info("Loading extension: " + extensionClass.getName() + " (" + path.getFileName().toString() + ")");

        Extension extension = null;

        try {
            Constructor<? extends Extension> constructor = extensionClass.getConstructor(FloraCorePlugin.class);
            extension = constructor.newInstance(this.plugin.getApiProvider());
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (extension == null) {
            try {
                Constructor<? extends Extension> constructor = extensionClass.getConstructor();
                extension = constructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unable to find valid constructor in " + extensionClass.getName());
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        this.extensions.add(new LoadedExtension(extension, path));
        extension.load();
        return extension;
    }

    @Override
    public @NonNull Collection<Extension> getLoadedExtensions() {
        return this.extensions.stream().map(e -> e.instance).collect(Collectors.toSet());
    }

    private static final class LoadedExtension {
        private final Extension instance;
        private final Path path;

        private LoadedExtension(Extension instance, Path path) {
            this.instance = instance;
            this.path = path;
        }
    }
}
