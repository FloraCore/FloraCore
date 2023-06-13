package team.floracore.bungee.loader;

import net.md_5.bungee.api.plugin.Plugin;
import team.floracore.bungee.FCBungeeBootstrap;
import team.floracore.common.loader.LoaderBootstrap;

public class BungeeLoaderPlugin extends Plugin {
    private final LoaderBootstrap plugin;

    public BungeeLoaderPlugin() {
        this.plugin = new FCBungeeBootstrap(this);
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
