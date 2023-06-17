package team.floracore.bungee.loader;

import net.md_5.bungee.api.plugin.Plugin;
import team.floracore.common.loader.JarInJarClassLoader;
import team.floracore.common.loader.LoaderBootstrap;

public class BungeeLoaderPlugin extends Plugin {
    private static final String JAR_NAME = "floracore-bungee.jarinjar";
    private static final String BOOTSTRAP_CLASS = "team.floracore.bungee.FCBungeeBootstrap";
    private final LoaderBootstrap plugin;

    public BungeeLoaderPlugin() {
        JarInJarClassLoader loader = new JarInJarClassLoader(getClass().getClassLoader(), JAR_NAME);
        this.plugin = loader.instantiatePlugin(BOOTSTRAP_CLASS, Plugin.class, this);
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
