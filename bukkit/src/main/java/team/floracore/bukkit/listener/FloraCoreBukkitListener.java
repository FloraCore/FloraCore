package team.floracore.bukkit.listener;

import org.bukkit.event.Listener;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.common.listener.AbstractFloraCoreListener;

public class FloraCoreBukkitListener extends AbstractFloraCoreListener implements Listener {
	private final FCBukkitPlugin plugin;

	public FloraCoreBukkitListener(FCBukkitPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@Override
	public FCBukkitPlugin getPlugin() {
		return plugin;
	}
}
