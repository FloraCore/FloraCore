package team.floracore.paper;

import org.bukkit.entity.*;
import org.bukkit.plugin.java.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.paper.command.*;
import team.floracore.paper.config.*;
import team.floracore.paper.listener.*;

import java.util.*;
import java.util.stream.*;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBukkitPlugin extends AbstractFloraCorePlugin {
    private final FCBukkitBootstrap bootstrap;
    private ListenerManager listenerManager;
    private CommandManager commandManager;
    private BukkitSenderFactory senderFactory;

    public FCBukkitPlugin(FCBukkitBootstrap bootstrap) {
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

    private static boolean isBrigadierSupported() {
        return classExists("com.mojang.brigadier.CommandDispatcher");
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
        return dependencies;
    }

    @Override
    public FCBukkitBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public JavaPlugin getLoader() {
        return this.bootstrap.getLoader();
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    @Override
    public String getServerName() {
        return getConfiguration().get(PaperConfigKeys.SERVER_NAME);
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig("config.yml").toFile());
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BukkitSenderFactory(this);
    }

    @Override
    protected void setupFramework() {
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
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

    public BukkitSenderFactory getSenderFactory() {
        return this.senderFactory;
    }
}
