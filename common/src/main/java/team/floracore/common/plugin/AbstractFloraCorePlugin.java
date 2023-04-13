package team.floracore.common.plugin;

import team.floracore.common.api.*;
import team.floracore.common.config.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.extension.*;
import team.floracore.common.plugin.logging.*;
import team.floracore.common.storage.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;

public abstract class AbstractFloraCorePlugin implements FloraCorePlugin {
    // init during load
    private DependencyManager dependencyManager;

    // init during enable
    private FloraCoreConfiguration configuration;
    private FloraCoreApiProvider apiProvider;
    private Storage storage;
    private SimpleExtensionManager extensionManager;

    /**
     * Performs the initial actions to load the plugin
     */
    public final void onLoad() {
        // load dependencies
        this.dependencyManager = createDependencyManager();
    }

    public final void onEnable() {
        // load configuration
        getLogger().info("Loading configuration...");
        ConfigurationAdapter configFileAdapter = provideConfigurationAdapter();
        this.configuration = new FloraCoreConfiguration(this, new MultiConfigurationAdapter(this, new SystemPropertyConfigAdapter(this), new EnvironmentVariableConfigAdapter(this), configFileAdapter));

        // now the configuration is loaded, we can create a storage factory and load initial dependencies
        StorageFactory storageFactory = new StorageFactory(this);
        // @formatter:off
        this.dependencyManager.loadStorageDependencies(
                storageFactory.getRequiredTypes(),
                getConfiguration().get(ConfigKeys.REDIS_ENABLED)
        );

        // initialise storage
        this.storage = storageFactory.getInstance();

        // register with the FC API
        this.apiProvider = new FloraCoreApiProvider(this);
        this.apiProvider.ensureApiWasLoadedByPlugin();
        ApiRegistrationUtil.registerProvider(this.apiProvider);

        // setup extension manager
        this.extensionManager = new SimpleExtensionManager(this);
        this.extensionManager.loadExtensions(getBootstrap().getConfigDirectory().resolve("extensions"));

        Duration timeTaken = Duration.between(getBootstrap().getStartupTime(), Instant.now());
        getLogger().info("Successfully enabled. (took " + timeTaken.toMillis() + "ms)");
    }

    public final void onDisable() {
        getLogger().info("Starting shutdown process...");

        // cancel delayed/repeating tasks
        getBootstrap().getScheduler().shutdownScheduler();

        // shutdown async executor pool
        getBootstrap().getScheduler().shutdownExecutor();

        // close isolated loaders for non-relocated dependencies
        getDependencyManager().close();
        // close classpath appender
        getBootstrap().getClassPathAppender().close();

        getLogger().info("Goodbye!");
    }

    protected abstract ConfigurationAdapter provideConfigurationAdapter();

    protected Path resolveConfig(String fileName) {
        Path configFile = getBootstrap().getConfigDirectory().resolve(fileName);

        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }

            try (InputStream is = getBootstrap().getResourceStream(fileName)) {
                Files.copy(is, configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return configFile;
    }

    @Override
    public FloraCoreConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public Storage getStorage() {
        return this.storage;
    }

    protected DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }

    @Override
    public DependencyManager getDependencyManager() {
        return this.dependencyManager;
    }

    @Override
    public PluginLogger getLogger() {
        return getBootstrap().getPluginLogger();
    }

    @Override
    public FloraCoreApiProvider getApiProvider() {
        return this.apiProvider;
    }

    @Override
    public SimpleExtensionManager getExtensionManager() {
        return this.extensionManager;
    }
}
