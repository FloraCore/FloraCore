package team.floracore.bukkit;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.*;
import org.floracore.api.server.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.inevntory.*;
import team.floracore.bukkit.listener.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.module.*;
import team.floracore.bukkit.util.nothing.*;
import team.floracore.common.config.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.sql.*;
import java.util.*;
import java.util.stream.*;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBukkitPlugin extends AbstractFloraCorePlugin {
    private static InventoryManager inventoryManager;
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
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig("config.yml").toFile());
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BukkitSenderFactory(this);
    }

    public static InventoryManager getInventoryManager() {
        return inventoryManager;
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

    @Override
    protected void setupFramework() {
        getLogger().info("Loading inventory manager...");
        inventoryManager = new InventoryManager(getLoader());
        inventoryManager.init();

        RegistrarRegistrar.instance.load();
        ListenerRegistrar.instance.load();
        IModule.ModuleModule.instance.load();
        NothingRegistrar.instance.load();

        Bukkit.getScheduler().runTaskTimerAsynchronously(getBootstrap().getLoader(), () -> {
            SERVER server = getStorage().getImplementation().selectServer(getServerName());
            if (server == null) {
                ServerType serverType = getConfiguration().get(ConfigKeys.SERVER_TYPE);
                server = new SERVER(this, getStorage().getImplementation(), -1, getServerName(), serverType, serverType.isAutoSync1(), serverType.isAutoSync2(), System.currentTimeMillis());
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
    }
}
