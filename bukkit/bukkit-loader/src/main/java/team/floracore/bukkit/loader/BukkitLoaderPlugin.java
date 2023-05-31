package team.floracore.bukkit.loader;

import org.bukkit.plugin.java.JavaPlugin;
import team.floracore.common.loader.JarInJarClassLoader;
import team.floracore.common.loader.LoaderBootstrap;

public class BukkitLoaderPlugin extends JavaPlugin {
    private static final String JAR_NAME = "floracore-bukkit.jarinjar";
    private static final String BOOTSTRAP_CLASS = "team.floracore.bukkit.FCBukkitBootstrap";

    private final LoaderBootstrap plugin;

    public BukkitLoaderPlugin() {
        JarInJarClassLoader loader = new JarInJarClassLoader(getClass().getClassLoader(), JAR_NAME);
        this.plugin = loader.instantiatePlugin(BOOTSTRAP_CLASS, JavaPlugin.class, this);
    }

    @Override
    public void onLoad() {
        this.plugin.onLoad();
    }

    @Override
    public void onDisable() {
        this.plugin.onDisable();
    }

    @Override
    public void onEnable() {
        this.plugin.onEnable();
    }

}
