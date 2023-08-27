package team.floracore.common.plugin;

import okhttp3.OkHttpClient;
import team.floracore.common.api.ApiRegistrationUtil;
import team.floracore.common.api.FloraCoreApiProvider;
import team.floracore.common.config.ConfigKeys;
import team.floracore.common.config.FloraCoreConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.dependencies.Dependency;
import team.floracore.common.dependencies.DependencyManager;
import team.floracore.common.dependencies.DependencyManagerImpl;
import team.floracore.common.extension.SimpleExtensionManager;
import team.floracore.common.http.BytebinClient;
import team.floracore.common.http.BytesocksClient;
import team.floracore.common.locale.data.DataManager;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.locale.translation.TranslationRepository;
import team.floracore.common.messaging.InternalMessagingService;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.common.plugin.logging.PluginLogger;
import team.floracore.common.storage.Storage;
import team.floracore.common.storage.StorageFactory;
import team.floracore.common.util.github.GithubUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class AbstractFloraCorePlugin implements FloraCorePlugin {
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
	private BytebinClient bytebin;
	private BytesocksClient bytesocks;
	private TranslationRepository translationRepository;
	private StorageFactory storageFactory;

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
		ret.add(Dependency.ADVENTURE_TEXT_SERIALIZER_JSON);
		ret.add(Dependency.ADVENTURE_TEXT_SERIALIZER_JSON_LEGACY_IMPL);
		ret.add(Dependency.ADVENTURE_TEXT_SERIALIZER_PLAIN);
		ret.add(Dependency.EXAMINATION_API);
		ret.add(Dependency.COMMONS_IO);
		ret.add(Dependency.CLOUD_CORE);
		ret.add(Dependency.CLOUD_ANNOTATIONS);
		ret.add(Dependency.CLOUD_BRIGADIER);
		ret.add(Dependency.CLOUD_SERVICES);
		ret.add(Dependency.CLOUD_TASKS);
		ret.add(Dependency.BSTATS_BASE);
		ret.add(Dependency.GEANTYREF);
		ret.add(Dependency.OKHTTP);
		ret.add(Dependency.OKIO);
		ret.add(Dependency.CAFFEINE);
		ret.add(Dependency.UNSAFE_ACCESSOR);
		ret.add(Dependency.OPENCSV);
		ret.add(Dependency.CROWDIN);
		ret.add(Dependency.HTTP_CORE);
		ret.add(Dependency.HTTP_CLIENT);
		ret.add(Dependency.JACKSON_DATABIND);
		ret.add(Dependency.JACKSON_CORE);
		ret.add(Dependency.JACKSON_ANNOTATIONS);
		ret.add(Dependency.COMMONS_LOGGING);
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
		this.configuration = new FloraCoreConfiguration(this, configFileAdapter);
		this.configuration.reload();
		setupConfiguration();

		// check update
		if (this.configuration.get(ConfigKeys.CHECK_UPDATE)) {
			this.getBootstrap().getScheduler().async().execute(() -> checkUpdate(true));
			this.getBootstrap().getScheduler().asyncRepeating(() -> checkUpdate(false), 10, TimeUnit.MINUTES);
		}

		// set up a byte bin instance
		this.httpClient = new OkHttpClient.Builder().callTimeout(15, TimeUnit.SECONDS).build();
		this.bytebin = new BytebinClient(this.httpClient, getConfiguration().get(ConfigKeys.BYTEBIN_URL), "floracore");
		this.bytesocks = new BytesocksClient(this.httpClient, getConfiguration().get(ConfigKeys.BYTESOCKS_HOST),
				"floracore/socks");

		// init translation repo and update bundle files
		this.translationRepository = new TranslationRepository(this);
		this.translationRepository.scheduleRefresh();
		this.translationRepository.scheduleRefreshRepeating();

		// now the configuration is loaded, we can create a storage factory and load initial dependencies
		this.storageFactory = new StorageFactory(this);
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
		expandApi();

		// setup extension manager
		this.extensionManager = new SimpleExtensionManager(this);
		this.extensionManager.loadExtensions(getBootstrap().getConfigDirectory().resolve("extensions"));

		Duration timeTaken = Duration.between(getBootstrap().getStartupTime(), Instant.now());
		getLogger().info("Successfully enabled. (took " + timeTaken.toMillis() + "ms)");
	}

	protected abstract void setupSenderFactory();

	protected abstract ConfigurationAdapter provideConfigurationAdapter();

	protected abstract MessagingFactory<?> provideMessagingFactory();

	protected abstract void setupFramework();


	protected abstract void expandApi();

	protected abstract void setupConfiguration();

	public final void onDisable() {
		getLogger().info("Starting shutdown process...");

		// cancel delayed/repeating tasks
		getBootstrap().getScheduler().shutdownScheduler();

		// close messaging service
		if (this.messagingService != null) {
			getLogger().info("Closing messaging service...");
			this.messagingService.close();
		}

		getLogger().info("Disabling framework...");
		disableFramework();

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

	protected abstract void disableFramework();

	protected Path resolveConfig(String fileName) {
		Path configFile = getBootstrap().getConfigDirectory().toAbsolutePath().resolve(fileName);

		// if the config doesn't exist, create it based on the template in the resources' dir
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
	public Storage getStorage() {
		return this.storage;
	}

	@Override
	public void setStorage(Storage storage) {
		this.storage = storage;
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

	@Override
	public StorageFactory getStorageFactory() {
		return storageFactory;
	}

	public void checkUpdate(boolean latestNotification) {
		try {
			if (latestNotification) {
				MiscMessage.STARTUP_CHECKING_UPDATE.send(getConsoleSender(), getBootstrap());
			}
			String leastReleaseTagVersion = GithubUtil.getLeastReleaseTagVersion();
			if (!GithubUtil.isLatestVersion(leastReleaseTagVersion, this.getBootstrap().getVersion())) {
				MiscMessage.STARTUP_CHECKING_UPDATE_OUTDATED.send(getConsoleSender(),
						getBootstrap(),
						leastReleaseTagVersion);
			} else {
				if (latestNotification) {
					MiscMessage.STARTUP_CHECKING_UPDATE_NEWEST.send(getConsoleSender(), getBootstrap());
				}
			}
		} catch (IOException e) {
			MiscMessage.STARTUP_CHECKING_UPDATE_FAILED.send(getConsoleSender(), getBootstrap());
		}
	}

	@Override
	public BytebinClient getBytebin() {
		return bytebin;
	}

	@Override
	public BytesocksClient getBytesocks() {
		return bytesocks;
	}
}
