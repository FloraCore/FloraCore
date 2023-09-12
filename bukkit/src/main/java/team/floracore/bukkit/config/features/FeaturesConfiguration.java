package team.floracore.bukkit.config.features;

import lombok.Getter;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;

@Getter
public class FeaturesConfiguration extends KeyedConfiguration {
	private final FCBukkitPlugin plugin;

	public FeaturesConfiguration(FCBukkitPlugin plugin, ConfigurationAdapter adapter) {
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

}
