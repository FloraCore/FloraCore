package team.floracore.bungee;

import com.google.gson.*;
import net.md_5.bungee.api.plugin.*;
import team.floracore.bungee.command.*;
import team.floracore.bungee.listener.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.messaging.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import java.util.*;
import java.util.stream.*;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBungeePlugin extends AbstractFloraCorePlugin {
    private final FCBungeeBootstrap bootstrap;
    private BungeeSenderFactory senderFactory;
    private ListenerManager listenerManager;
    private CommandManager commandManager;
    private BungeeMessagingFactory bungeeMessagingFactory;

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

    public BungeeSenderFactory getSenderFactory() {
        return this.senderFactory;
    }
}
