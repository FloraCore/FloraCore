package team.floracore.plugin.loader;

import org.bukkit.plugin.java.*;
import team.floracore.common.loader.*;

public class BukkitLoaderPlugin extends JavaPlugin {
    private static final String JAR_NAME = "floracore-plugin.jarinjar";
    private static final String BOOTSTRAP_CLASS = "team.floracore.plugin.FCBukkitBootstrap";

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
    public void onEnable() {
        this.plugin.onEnable();
    }

    @Override
    public void onDisable() {
        this.plugin.onDisable();
    }

}
