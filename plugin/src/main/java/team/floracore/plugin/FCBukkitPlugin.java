package team.floracore.plugin;

import org.bukkit.entity.*;
import org.bukkit.plugin.java.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.dependencies.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import java.util.*;
import java.util.stream.*;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBukkitPlugin extends AbstractFloraCorePlugin {
    private final FCBukkitBootstrap bootstrap;
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

    @Override
    public Stream<Sender> getOnlineSenders() {
        List<Player> players = new ArrayList<>(this.bootstrap.getServer().getOnlinePlayers());
        return Stream.concat(Stream.of(getConsoleSender()), players.stream().map(p -> getSenderFactory().wrap(p)));
    }

    @Override
    public Sender getConsoleSender() {
        return getSenderFactory().wrap(this.bootstrap.getConsole());
    }

    public BukkitSenderFactory getSenderFactory() {
        return this.senderFactory;
    }

    public JavaPlugin getLoader() {
        return this.bootstrap.getLoader();
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig("config.yml").toFile());
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BukkitSenderFactory(this);
    }
}
