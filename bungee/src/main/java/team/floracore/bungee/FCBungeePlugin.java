package team.floracore.bungee;

import com.google.gson.JsonElement;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import team.floracore.api.FloraCore;
import team.floracore.api.FloraCoreProvider;
import team.floracore.bungee.command.CommandManager;
import team.floracore.bungee.config.BungeeConfigAdapter;
import team.floracore.bungee.config.chat.ChatConfiguration;
import team.floracore.bungee.listener.ListenerManager;
import team.floracore.bungee.messaging.BungeeMessagingFactory;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.dependencies.Dependency;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.common.plugin.AbstractFloraCorePlugin;
import team.floracore.common.sender.Sender;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBungeePlugin extends AbstractFloraCorePlugin {
    private final FCBungeeBootstrap bootstrap;
    private BungeeSenderFactory senderFactory;
    private ListenerManager listenerManager;
    private CommandManager commandManager;
    private BungeeMessagingFactory bungeeMessagingFactory;
    private KeyedConfiguration chatConfiguration;

    public FCBungeePlugin(FCBungeeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public ProxyServer getProxy() {
        return getBootstrap().getProxy();
    }

    @Override
    public FCBungeeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Stream<Sender> getOnlineSenders() {
        return Stream.concat(
                Stream.of(getConsoleSender()),
                this.bootstrap.getProxy().getPlayers().stream().map(p -> this.senderFactory.wrap(p))
        );
    }

    @Override
    public Sender getConsoleSender() {
        return this.senderFactory.wrap(this.bootstrap.getProxy().getConsole());
    }

    @Override
    public boolean processIncomingMessage(String type, JsonElement content, UUID id) {
        return bungeeMessagingFactory.processIncomingMessage(type, content, id);
    }

    public Plugin getLoader() {
        return this.bootstrap.getLoader();
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        Set<Dependency> dependencies = super.getGlobalDependencies();
        dependencies.add(Dependency.ADVENTURE_PLATFORM_BUNGEECORD);
        dependencies.add(Dependency.ADVENTURE_TEXT_SERIALIZER_BUNGEECORD);
        dependencies.add(Dependency.CLOUD_BUNGEE);
        dependencies.add(Dependency.BSTATS_BUNGEE);
        return dependencies;
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BungeeSenderFactory(this);
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BungeeConfigAdapter(this, resolveConfig("config.yml").toFile());
    }

    @Override
    protected MessagingFactory<?> provideMessagingFactory() {
        this.bungeeMessagingFactory = new BungeeMessagingFactory(this);
        return bungeeMessagingFactory;
    }

    @Override
    protected void setupFramework() {
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
    }

    @Override
    protected void expandApi() {
        FloraCore floraCore = FloraCoreProvider.get();
        floraCore.getPlatform().setFloraCorePlatformPlugin(this);
    }

    @Override
    protected void setupConfiguration() {
        chatConfiguration = new ChatConfiguration(this, new BungeeConfigAdapter(this,
                resolveConfig("chat.yml").toFile())
        );
    }

    public KeyedConfiguration getChatConfiguration() {
        return chatConfiguration;
    }

    @Override
    protected void disableFramework() {

    }

    public BungeeMessagingFactory getBungeeMessagingFactory() {
        return bungeeMessagingFactory;
    }

    public BungeeSenderFactory getSenderFactory() {
        return this.senderFactory;
    }

    @Override
    public boolean luckPermsHook() {
        return getLoader().getProxy().getPluginManager().getPlugin("LuckPerms") != null;
    }

    @Override
    public Sender getSender(UUID uuid) {
        ProxiedPlayer player = getProxy().getPlayer(uuid);
        if (player == null) {
            return null;
        }
        return getSenderFactory().wrap(player);
    }
}
