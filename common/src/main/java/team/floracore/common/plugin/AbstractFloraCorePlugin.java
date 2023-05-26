package team.floracore.common.plugin;

import okhttp3.*;
import team.floracore.common.api.*;
import team.floracore.common.config.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.extension.*;
import team.floracore.common.locale.data.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.messaging.*;
import team.floracore.common.plugin.logging.*;
import team.floracore.common.storage.*;
import team.floracore.common.util.github.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractFloraCorePlugin implements FloraCorePlugin {
    // Active plugins on the server
    private final Map<String, List<String>> loadedPlugins = new HashMap<>();
    // init during load
    private DependencyManager dependencyManager;
    private TranslationManager translationManager;
    private DataManager dataManager;
    // init during enable
    private FloraCoreConfiguration configuration;
    private FloraCoreApiProvider apiProvider;
    private InternalMessagingService messagingService = null;
    private Storage storage;
    private SimpleExtensionManager extensionManager;
    private OkHttpClient httpClient;
    private TranslationRepository translationRepository;
    private NamesRepository namesRepository;

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

        // load data
        this.dataManager = new DataManager(this);
        this.dataManager.reload();
    }

    protected DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }

    protected Set<Dependency> getGlobalDependencies() {
        Set<Dependency> ret = EnumSet.of(Dependency.ADVENTURE, Dependency.ADVENTURE_NBT);
        ret.add(Dependency.BYTE_BUDDY_AGENT);
        ret.add(Dependency.ADVENTURE_KEY);
        ret.add(Dependency.ADVENTURE_PLATFORM_API);
        ret.add(Dependency.ADVENTURE_PLATFORM_FACET);
        ret.add(Dependency.ADVENTURE_TEXT_SERIALIZER_LEGACY);
        ret.add(Dependency.ADVENTURE_TEXT_SERIALIZER_GSON);
        ret.add(Dependency.ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY_IMPL);
        ret.add(Dependency.ADVENTURE_TEXT_SERIALIZER_PLAIN);
        ret.add(Dependency.EXAMINATION_API);
        ret.add(Dependency.CLOUD_CORE);
        ret.add(Dependency.CLOUD_ANNOTATIONS);
        ret.add(Dependency.CLOUD_BRIGADIER);
        ret.add(Dependency.CLOUD_SERVICES);
        ret.add(Dependency.CLOUD_TASKS);
        ret.add(Dependency.GEANTYREF);
        ret.add(Dependency.OKHTTP);
        ret.add(Dependency.OKIO);
        ret.add(Dependency.CAFFEINE);
        ret.add(Dependency.UNSAFE_ACCESSOR);
        ret.add(Dependency.OPENCSV);
        return ret;
    }

    public final void onEnable() {
        // load the sender factory instance
        setupSenderFactory();

        // send the startup banner
        MiscMessage.STARTUP_BANNER.send(getConsoleSender(), getBootstrap());

        // load configuration
        getLogger().info("Loading configuration...");
        ConfigurationAdapter configFileAdapter = provideConfigurationAdapter();
        this.configuration = new FloraCoreConfiguration(this,
                new MultiConfigurationAdapter(this,
                        new SystemPropertyConfigAdapter(this),
                        new EnvironmentVariableConfigAdapter(this),
                        configFileAdapter));

        // check update
        if (this.configuration.get(ConfigKeys.CHECK_UPDATE)) {
            this.getBootstrap().getScheduler().async().execute(() -> {
                try {
                    MiscMessage.STARTUP_CHECKING_UPDATE.send(getConsoleSender(), getBootstrap());
                    String leastReleaseTagVersion = GithubUtil.getLeastReleaseTagVersion();
                    if (!GithubUtil.isLatestVersion(leastReleaseTagVersion, this.getBootstrap().getVersion())) {
                        MiscMessage.STARTUP_CHECKING_UPDATE_OUTDATED.send(getConsoleSender(),
                                getBootstrap(),
                                leastReleaseTagVersion);
                    } else {
                        MiscMessage.STARTUP_CHECKING_UPDATE_NEWEST.send(getConsoleSender(), getBootstrap());
                    }
                } catch (IOException e) {
                    MiscMessage.STARTUP_CHECKING_UPDATE_FAILED.send(getConsoleSender(), getBootstrap());
                }
            });
        }

        // setup a bytebin instance
        this.httpClient = new OkHttpClient.Builder().callTimeout(15, TimeUnit.SECONDS).build();

        // init translation repo and update bundle files
        this.translationRepository = new TranslationRepository(this);
        this.translationRepository.scheduleRefresh();
        this.translationRepository.scheduleRefreshRepeating();

        // init data names repo
        this.namesRepository = new NamesRepository(this);
        this.namesRepository.scheduleRefresh();
        this.namesRepository.scheduleRefreshRepeating();

        // now the configuration is loaded, we can create a storage factory and load initial dependencies
        StorageFactory storageFactory = new StorageFactory(this);
        this.dependencyManager.loadStorageDependencies(storageFactory.getRequiredTypes(),
                getConfiguration().get(ConfigKeys.REDIS_ENABLED));

        // initialise storage
        this.storage = storageFactory.getInstance();
        this.messagingService = provideMessagingFactory().getInstance();

        getLogger().info("Loading framework...");
        setupFramework();

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

    protected abstract void setupSenderFactory();

    protected abstract ConfigurationAdapter provideConfigurationAdapter();

    protected abstract void setupFramework();

    protected abstract MessagingFactory<?> provideMessagingFactory();

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

        // shutdown okhttp
        this.httpClient.dispatcher().executorService().shutdown();
        this.httpClient.connectionPool().evictAll();

        // close isolated loaders for non-relocated dependencies
        getDependencyManager().close();
        // close classpath appender
        getBootstrap().getClassPathAppender().close();

        getLogger().info("Goodbye!");
    }

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
    public OkHttpClient getHttpClient() {
        return this.httpClient;
    }

    @Override
    public PluginLogger getLogger() {
        return getBootstrap().getPluginLogger();
    }

    @Override
    public DependencyManager getDependencyManager() {
        return this.dependencyManager;
    }

    @Override
    public FloraCoreConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public NamesRepository getNamesRepository() {
        return namesRepository;
    }

    @Override
    public Storage getStorage() {
        return this.storage;
    }

    @Override
    public FloraCoreApiProvider getApiProvider() {
        return this.apiProvider;
    }

    @Override
    public SimpleExtensionManager getExtensionManager() {
        return this.extensionManager;
    }

    @Override
    public TranslationManager getTranslationManager() {
        return this.translationManager;
    }

    @Override
    public TranslationRepository getTranslationRepository() {
        return this.translationRepository;
    }

    @Override
    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public Map<String, List<String>> getLoadedPlugins() {
        return loadedPlugins;
    }

    @Override
    public boolean isPluginInstalled(String name) {
        return loadedPlugins.containsKey(name);
    }

    @Override
    public boolean isPluginInstalled(String name, String author) {
        if (loadedPlugins.containsKey(name)) {
            return loadedPlugins.get(name).contains(author);
        }
        return false;
    }

    @Override
    public String getServerName() {
        return getConfiguration().get(ConfigKeys.SERVER_NAME);
    }

    @Override
    public Optional<InternalMessagingService> getMessagingService() {
        return Optional.ofNullable(this.messagingService);
    }

    @Override
    public void setMessagingService(InternalMessagingService messagingService) {
        if (this.messagingService == null) {
            this.messagingService = messagingService;
        }
    }
}
