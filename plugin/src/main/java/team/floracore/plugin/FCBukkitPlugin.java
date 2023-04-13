package team.floracore.plugin;

import org.bukkit.plugin.java.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.plugin.*;

/**
 * FloraCore implementation for the Bukkit API.
 */
public class FCBukkitPlugin extends AbstractFloraCorePlugin {
    private final FCBukkitBootstrap bootstrap;

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
    public FCBukkitBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public JavaPlugin getLoader() {
        return this.bootstrap.getLoader();
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig("config.yml").toFile());
    }
}
