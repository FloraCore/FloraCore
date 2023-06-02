package team.floracore.bukkit;

import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.floracore.api.server.ServerType;
import team.floracore.bukkit.command.CommandManager;
import team.floracore.bukkit.config.BoardsConfiguration;
import team.floracore.bukkit.inevntory.InventoryManager;
import team.floracore.bukkit.listener.ListenerManager;
import team.floracore.bukkit.locale.chat.ChatManager;
import team.floracore.bukkit.messaging.BukkitMessagingFactory;
import team.floracore.bukkit.scoreboard.ScoreBoardManager;
import team.floracore.bukkit.util.BungeeUtil;
import team.floracore.bukkit.util.ListenerRegistrar;
import team.floracore.bukkit.util.module.IModule;
import team.floracore.bukkit.util.module.RegistrarRegistrar;
import team.floracore.bukkit.util.nothing.NothingRegistrar;
import team.floracore.common.config.ConfigKeys;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.dependencies.Dependency;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.common.plugin.AbstractFloraCorePlugin;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.SERVER;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBukkitPlugin extends AbstractFloraCorePlugin {
    private static InventoryManager inventoryManager;
    private final FCBukkitBootstrap bootstrap;
    private ListenerManager listenerManager;
    private CommandManager commandManager;
    private BukkitSenderFactory senderFactory;
    private BukkitMessagingFactory bukkitMessagingFactory;
    private ChatManager chatManager;
    private BungeeUtil bungeeUtil;
    private KeyedConfiguration boardsConfiguration;
    private ScoreBoardManager scoreBoardManager;

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

    public static InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        Set<Dependency> dependencies = super.getGlobalDependencies();
        dependencies.add(Dependency.ADVENTURE_PLATFORM_BUKKIT);
        dependencies.add(Dependency.CLOUD_BUKKIT);
        dependencies.add(Dependency.CLOUD_PAPER);
        return dependencies;
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BukkitSenderFactory(this);
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig("config.yml").toFile());
    }

    @Override
    protected void setupFramework() {
        this.bungeeUtil = new BungeeUtil(this);

        RegistrarRegistrar.instance.load();
        ListenerRegistrar.instance.load();
        IModule.ModuleModule.instance.load();
        NothingRegistrar.instance.load();

        getLogger().info("Loading inventory manager...");
        inventoryManager = new InventoryManager(getLoader());
        inventoryManager.init();

        Bukkit.getScheduler().runTaskTimerAsynchronously(getBootstrap().getLoader(), () -> {
            SERVER server = getStorage().getImplementation().selectServer(getServerName());
            if (server == null) {
                ServerType serverType = getConfiguration().get(ConfigKeys.SERVER_TYPE);
                server = new SERVER(this,
                        getStorage().getImplementation(),
                        -1,
                        getServerName(),
                        serverType,
                        serverType.isAutoSync1(),
                        serverType.isAutoSync2(),
                        System.currentTimeMillis());
                try {
                    server.init();
                } catch (SQLException e) {
                    throw new RuntimeException();
                }
            } else {
                server.setLastActiveTime(System.currentTimeMillis());
            }
        }, 0, 20 * 60 * 10);

        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
        this.chatManager = new ChatManager(this);
    }

    @Override
    public MessagingFactory<?> provideMessagingFactory() {
        this.bukkitMessagingFactory = new BukkitMessagingFactory(this);
        return this.bukkitMessagingFactory;
    }

    @Override
    protected void setupConfiguration() {
        this.boardsConfiguration = new BoardsConfiguration(this,
                new BukkitConfigAdapter(this,
                        resolveConfig("boards.yml").toFile()));
        getLogger().info("Loading scoreboard manager...");
        scoreBoardManager = new ScoreBoardManager(this);
        scoreBoardManager.start();
    }

    @Override
    public boolean luckPermsHook() {
        return getLoader().getServer().getPluginManager().getPlugin("LuckPerms") != null;
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

        scoreBoardManager.getSidebarBoard().cancel();

        chatManager.shutdown();
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

    public BukkitSenderFactory getSenderFactory() {
        return this.senderFactory;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public BukkitMessagingFactory getBukkitMessagingFactory() {
        return bukkitMessagingFactory;
    }

    public BungeeUtil getBungeeUtil() {
        return bungeeUtil;
    }

    public KeyedConfiguration getBoardsConfiguration() {
        return boardsConfiguration;
    }

    public ScoreBoardManager getScoreBoardManager() {
        return scoreBoardManager;
    }
}
