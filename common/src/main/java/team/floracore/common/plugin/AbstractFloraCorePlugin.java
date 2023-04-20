package team.floracore.common.plugin;

import net.kyori.adventure.platform.bukkit.*;
import okhttp3.*;
import team.floracore.common.api.*;
import team.floracore.common.command.*;
import team.floracore.common.config.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.extension.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.logging.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractFloraCorePlugin implements FloraCorePlugin {
    // init during load
    private DependencyManager dependencyManager;
    private TranslationManager translationManager;

    // init during enable
    private FloraCoreConfiguration configuration;
    private FloraCoreApiProvider apiProvider;
    private Storage storage;
    private SimpleExtensionManager extensionManager;
    private BukkitAudiences bukkitAudiences;
    private CommandManager commandManager;
    private OkHttpClient httpClient;
    private TranslationRepository translationRepository;
    private BukkitSenderFactory senderFactory;

    /**
     * Performs the initial actions to load the plugin
     */
    public final void onLoad() {
        // load dependencies
        this.dependencyManager = createDependencyManager();
        this.dependencyManager.loadDependencies(getGlobalDependencies());

        // load translations
        this.translationManager = new TranslationManager(this);
        this.translationManager.reload();
    }

    public final void onEnable() {
        this.bukkitAudiences = BukkitAudiences.create(getBootstrap().getPlugin());

        // load the sender factory instance
        this.senderFactory = new BukkitSenderFactory(this);

        // send the startup banner
        Message.STARTUP_BANNER.send(getConsoleSender(), getBootstrap());

        // load configuration
        getLogger().info("Loading configuration...");
        ConfigurationAdapter configFileAdapter = provideConfigurationAdapter();
        this.configuration = new FloraCoreConfiguration(this, new MultiConfigurationAdapter(this, new SystemPropertyConfigAdapter(this), new EnvironmentVariableConfigAdapter(this), configFileAdapter));


        // setup a bytebin instance
        this.httpClient = new OkHttpClient.Builder().callTimeout(15, TimeUnit.SECONDS).build();

        // init translation repo and update bundle files
        this.translationRepository = new TranslationRepository(this);
        this.translationRepository.scheduleRefresh();

        // now the configuration is loaded, we can create a storage factory and load initial dependencies
        StorageFactory storageFactory = new StorageFactory(this);
        this.dependencyManager.loadStorageDependencies(storageFactory.getRequiredTypes(), getConfiguration().get(ConfigKeys.REDIS_ENABLED));

        // initialise storage
        this.storage = storageFactory.getInstance();

        getLogger().info("Loading cloud command framework...");
        this.commandManager = new CommandManager(this);

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

        // close storage
        getLogger().info("Closing storage...");
        this.storage.shutdown();

        // unregister api
        ApiRegistrationUtil.unregisterProvider();

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

    protected Set<Dependency> getGlobalDependencies() {
        // @formatter:off
        return EnumSet.of(Dependency.ADVENTURE,
                Dependency.ADVENTURE_NBT,
                Dependency.ADVENTURE_KEY,
                Dependency.ADVENTURE_PLATFORM_API,
                Dependency.ADVENTURE_PLATFORM_FACET,
                Dependency.ADVENTURE_TEXT_SERIALIZER_LEGACY,
                Dependency.ADVENTURE_TEXT_SERIALIZER_GSON,
                Dependency.ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY_IMPL,
                Dependency.ADVENTURE_TEXT_SERIALIZER_PLAIN,
                Dependency.EXAMINATION_API,
                Dependency.CLOUD_CORE,
                Dependency.CLOUD_ANNOTATIONS,
                Dependency.CLOUD_BRIGADIER,
                Dependency.CLOUD_SERVICES,
                Dependency.CLOUD_TASKS,
                Dependency.GEANTYREF,
                Dependency.INVENTORY_FRAMEWORK,
                Dependency.OKHTTP,
                Dependency.OKIO);
    }

    @Override
    public TranslationManager getTranslationManager() {
        return this.translationManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public BukkitAudiences getBukkitAudiences() {
        return this.bukkitAudiences;
    }
    @Override
    public BukkitSenderFactory getSenderFactory() {
        return senderFactory;
    }
@Override
    public FloraCoreConfiguration getConfiguration() {
        return this.configuration;
    }
    @Override
    public TranslationRepository getTranslationRepository() {
        return this.translationRepository;
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
    public OkHttpClient getHttpClient() {
        return this.httpClient;
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
