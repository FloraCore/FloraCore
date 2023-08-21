package team.floracore.bukkit.loader;

import org.bukkit.plugin.java.JavaPlugin;
import team.floracore.bukkit.FCBukkitBootstrap;
import team.floracore.common.loader.LoaderBootstrap;

public class BukkitLoaderPlugin extends JavaPlugin {
	private final LoaderBootstrap plugin;

	public BukkitLoaderPlugin() {
		this.plugin = new FCBukkitBootstrap(this);
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
