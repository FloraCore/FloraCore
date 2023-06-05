package team.floracore.common.dependencies;

import com.google.common.collect.ImmutableSet;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import team.floracore.common.dependencies.classloader.IsolatedClassLoader;
import team.floracore.common.dependencies.relocation.Relocation;
import team.floracore.common.dependencies.relocation.RelocationHandler;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.plugin.classpath.ClassPathAppender;
import team.floracore.common.storage.StorageType;
import team.floracore.common.util.MoreFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * Loads and manages runtime dependencies for the plugin.
 */
public class DependencyManagerImpl implements DependencyManager {

    /**
     * A registry containing plugin specific behaviour for dependencies.
     */
    private final DependencyRegistry registry;
    /**
     * The path where library jars are cached.
     */
    private final Path cacheDirectory;
    /**
     * The classpath appender to preload dependencies into
     */
    private final ClassPathAppender classPathAppender;
    /**
     * The executor to use when loading dependencies
     */
    private final Executor loadingExecutor;

    /**
     * A map of dependencies which have already been loaded.
     */
    private final EnumMap<Dependency, Path> loaded = new EnumMap<>(Dependency.class);
    /**
     * A map of isolated classloaders which have been created.
     */
    private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    /**
     * Cached relocation handler instance.
     */
    private @MonotonicNonNull RelocationHandler relocationHandler = null;

    public DependencyManagerImpl(FloraCorePlugin plugin) {
        this.registry = new DependencyRegistry();
        this.cacheDirectory = setupCacheDirectory(plugin);
        this.classPathAppender = plugin.getBootstrap().getClassPathAppender();
        this.loadingExecutor = plugin.getBootstrap().getScheduler().async();
    }

    private static Path setupCacheDirectory(FloraCorePlugin plugin) {
        Path cacheDirectory = plugin.getBootstrap().getDataDirectory().resolve("libs");
        try {
            MoreFiles.createDirectoriesIfNotExists(cacheDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create libs directory", e);
        }

        Path oldCacheDirectory = plugin.getBootstrap().getDataDirectory().resolve("lib");
        if (Files.exists(oldCacheDirectory)) {
            try {
                MoreFiles.deleteDirectory(oldCacheDirectory);
            } catch (IOException e) {
                plugin.getLogger().warn("Unable to delete lib directory", e);
            }
        }

        return cacheDirectory;
    }

    private synchronized RelocationHandler getRelocationHandler() {
        if (this.relocationHandler == null) {
            this.relocationHandler = new RelocationHandler();
        }
        return this.relocationHandler;
    }

    @Override
    public ClassLoader obtainClassLoaderWith(Set<Dependency> dependencies) {
        ImmutableSet<Dependency> set = ImmutableSet.copyOf(dependencies);

        for (Dependency dependency : dependencies) {
            if (!this.loaded.containsKey(dependency)) {
                throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
            }
        }

        synchronized (this.loaders) {
            IsolatedClassLoader classLoader = this.loaders.get(set);
            if (classLoader != null) {
                return classLoader;
            }

            URL[] urls = set.stream()
                    .map(this.loaded::get)
                    .map(file -> {
                        try {
                            return file.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);

            classLoader = new IsolatedClassLoader(urls);
            this.loaders.put(set, classLoader);
            return classLoader;
        }
    }

    @Override
    public void loadDependencies(Set<Dependency> dependencies) {
        CountDownLatch latch = new CountDownLatch(dependencies.size());

        for (Dependency dependency : dependencies) {
            if (this.loaded.containsKey(dependency)) {
                latch.countDown();
                continue;
            }

            this.loadingExecutor.execute(() -> {
                try {
                    loadDependency(dependency);
                } catch (Throwable e) {
                    new RuntimeException("Unable to load dependency " + dependency.name(), e).printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void loadStorageDependencies(Set<StorageType> storageTypes, boolean redis) {
        loadDependencies(this.registry.resolveStorageDependencies(storageTypes, redis));
    }

    private void loadDependency(Dependency dependency) throws Exception {
        if (this.loaded.containsKey(dependency)) {
            return;
        }

        Path file = remapDependency(dependency, downloadDependency(dependency));

        this.loaded.put(dependency, file);

        if (this.classPathAppender != null && this.registry.shouldAutoLoad(dependency)) {
            this.classPathAppender.addJarToClasspath(file);
        }
    }

    private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
        Path file = this.cacheDirectory.resolve(dependency.getFileName(null));

        // if the file already exists, don't attempt to re-download it.
        if (Files.exists(file)) {
            return file;
        }

        DependencyDownloadException lastError = null;

        // attempt to download the dependency from each repo in order.
        for (DependencyRepository repo : DependencyRepository.values()) {
            try {
                repo.download(dependency, file);
                return file;
            } catch (DependencyDownloadException e) {
                lastError = e;
            }
        }

        throw Objects.requireNonNull(lastError);
    }

    private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
        List<Relocation> rules = new ArrayList<>(dependency.getRelocations());

        if (rules.isEmpty()) {
            return normalFile;
        }

        Path remappedFile = this.cacheDirectory.resolve(dependency.getFileName(DependencyRegistry.isGsonRelocated() ?
                "remapped-legacy" : "remapped"));

        // if the remapped source exists already, just use that.
        if (Files.exists(remappedFile)) {
            return remappedFile;
        }

        getRelocationHandler().remap(normalFile, remappedFile, rules);
        return remappedFile;
    }

    @Override
    public void close() {
        IOException firstEx = null;

        for (IsolatedClassLoader loader : this.loaders.values()) {
            try {
                loader.close();
            } catch (IOException ex) {
                if (firstEx == null) {
                    firstEx = ex;
                } else {
                    firstEx.addSuppressed(ex);
                }
            }
        }

        if (firstEx != null) {
            firstEx.printStackTrace();
        }
    }

}
