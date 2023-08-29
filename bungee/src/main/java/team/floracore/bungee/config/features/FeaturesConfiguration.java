package team.floracore.bungee.config.features;

import team.floracore.bungee.FCBungeePlugin;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.plugin.FloraCorePlugin;

public class FeaturesConfiguration extends KeyedConfiguration {
	private final FCBungeePlugin plugin;

	public FeaturesConfiguration(FCBungeePlugin plugin, ConfigurationAdapter adapter) {
		super(adapter, FeaturesKeys.getKeys());
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
