package team.floracore.waterfall.loader;

import net.md_5.bungee.api.plugin.*;
import team.floracore.common.loader.*;

public class WaterfallLoaderPlugin extends Plugin {
    private static final String JAR_NAME = "floracore-waterfall.jarinjar";
    private static final String BOOTSTRAP_CLASS = "team.floracore.waterfall.FCBungeeBootstrap";

    private final LoaderBootstrap plugin;

    public WaterfallLoaderPlugin() {
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
