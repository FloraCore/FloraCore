package team.floracore.common.config;

import lombok.Getter;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.plugin.FloraCorePlugin;

@Getter
public class FloraCoreConfiguration extends KeyedConfiguration {
	private final FloraCorePlugin plugin;

	public FloraCoreConfiguration(FloraCorePlugin plugin, ConfigurationAdapter adapter) {
		super(adapter, ConfigKeys.getKeys());
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
