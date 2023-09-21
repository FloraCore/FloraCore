package team.floracore.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.JsonElement;
import lombok.Getter;
import me.huanmeng.opensource.bukkit.gui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.floracore.api.FloraCore;
import org.floracore.api.FloraCoreProvider;
import team.floracore.bukkit.command.CommandManager;
import team.floracore.bukkit.config.BukkitConfigAdapter;
import team.floracore.bukkit.config.features.FeaturesConfiguration;
import team.floracore.bukkit.hooks.placeholderapi.PlaceholderAPIHook;
import team.floracore.bukkit.listener.ListenerManager;
import team.floracore.bukkit.messaging.BukkitMessagingFactory;
import team.floracore.bukkit.util.BungeeUtil;
import team.floracore.bukkit.util.ListenerRegistrar;
import team.floracore.bukkit.util.module.IModule;
import team.floracore.bukkit.util.module.RegistrarRegistrar;
import team.floracore.bukkit.util.nothing.NothingRegistrar;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.dependencies.Dependency;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.common.plugin.AbstractFloraCorePlugin;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBukkitPlugin extends AbstractFloraCorePlugin {
	private final FCBukkitBootstrap bootstrap;
	@Getter
	private ListenerManager listenerManager;
	@Getter
	private CommandManager commandManager;
	@Getter
	private BukkitSenderFactory senderFactory;
	@Getter
	private BukkitMessagingFactory bukkitMessagingFactory;
	@Getter
	private BungeeUtil bungeeUtil;
	@Getter
	private KeyedConfiguration boardsConfiguration;
	@Getter
	private KeyedConfiguration featuresConfiguration;
	@Getter
	private GuiManager guiManager;
	@Getter
	private ProtocolManager protocolManager;
	private PlaceholderAPIHook placeholderAPIHook;

	public FCBukkitPlugin(FCBukkitBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	private static boolean isBrigadierSupported() {
		return classExists("com.mojang.brigadier.CommandDispatcher");
	}

	private static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private static boolean isAsyncTabCompleteSupported() {
		return classExists("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
	}

	@Override
	protected Set<Dependency> getGlobalDependencies() {
		Set<Dependency> dependencies = super.getGlobalDependencies();
		dependencies.add(Dependency.ADVENTURE_PLATFORM_BUKKIT);
		dependencies.add(Dependency.CLOUD_BUKKIT);
		dependencies.add(Dependency.CLOUD_PAPER);
		dependencies.add(Dependency.BUKKIT_GUI);
		dependencies.add(Dependency.BSTATS_BUKKIT);
		dependencies.add(Dependency.PAPER_LIB);
		return dependencies;
	}

	@Override
	protected void setupSenderFactory() {
		this.senderFactory = new BukkitSenderFactory(this);
	}

	@Override
	protected ConfigurationAdapter provideConfigurationAdapter(String fileName) {
		return new BukkitConfigAdapter(this, resolveConfig(fileName).toFile());
	}

	@Override
	protected void setupFramework() {
		this.bungeeUtil = new BungeeUtil(this);

		RegistrarRegistrar.instance.load();
		ListenerRegistrar.instance.load();
		IModule.ModuleModule.instance.load();
		NothingRegistrar.instance.load();
		protocolManager = ProtocolLibrary.getProtocolManager();

		getLogger().info("Loading inventory manager...");
		guiManager = new GuiManager(getLoader());

		this.listenerManager = new ListenerManager(this);
		this.commandManager = new CommandManager(this);

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("Hooking PlaceholderAPI...");
			this.placeholderAPIHook = new PlaceholderAPIHook(this);
			this.placeholderAPIHook.register();
		}
	}

	@Override
	protected void expandApi() {
		FloraCore floraCore = FloraCoreProvider.get();
		floraCore.getPlatform().setFloraCorePlatformPlugin(this);
	}

	@Override
	public MessagingFactory<?> provideMessagingFactory() {
		this.bukkitMessagingFactory = new BukkitMessagingFactory(this);
		return this.bukkitMessagingFactory;
	}

	@Override
	protected void setupConfiguration() {
		this.featuresConfiguration = new FeaturesConfiguration(this,
				new BukkitConfigAdapter(this,
						resolveConfig("features.yml").toFile()));
		getLogger().info("Loading scoreboard manager...");
	}

	@Override
	public boolean luckPermsHook() {
		return Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
	}

	@Override
	public Sender getSender(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			return null;
		}
		return getSenderFactory().wrap(player);
	}

	@Override
	protected void disableFramework() {
		RegistrarRegistrar.instance.unload();
		ListenerRegistrar.instance.unload();
		IModule.ModuleModule.instance.unload();
		NothingRegistrar.instance.unload();
	}

	public JavaPlugin getLoader() {
		return this.bootstrap.getLoader();
	}

	@Override
	public FCBukkitBootstrap getBootstrap() {
		return this.bootstrap;
	}

	@Override
	public Stream<Sender> getOnlineSenders() {
		List<Player> players = new ArrayList<>(this.bootstrap.getServer().getOnlinePlayers());
		return Stream.concat(
				Stream.of(getConsoleSender()),
				players.stream().map(p -> getSenderFactory().wrap(p))
		);
	}

	@Override
	public Sender getConsoleSender() {
		return getSenderFactory().wrap(this.bootstrap.getConsole());
	}

	@Override
	public boolean processIncomingMessage(String type, JsonElement content, UUID id) {
		return bukkitMessagingFactory.processIncomingMessage(type, content, id);
	}

}
