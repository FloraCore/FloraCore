package team.floracore.bukkit.config.boards;

import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.plugin.FloraCorePlugin;

public class BoardsConfiguration extends KeyedConfiguration {
	private final FCBukkitPlugin plugin;

	public BoardsConfiguration(FCBukkitPlugin plugin, ConfigurationAdapter adapter) {
		super(adapter, BoardsKeys.getKeys());
		this.plugin = plugin;

		init();
	}

	@Override
	protected void load(boolean initial) {
		super.load(initial);
	}

	@Override
	public void reload() {
		super.reload();
	}

	public FloraCorePlugin getPlugin() {
		return this.plugin;
	}
}
