package team.floracore.common.config;

import io.github.karlatemp.unsafeaccessor.Root;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.TypeUtil;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

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
		Component newPrefix;
		if (this.get(ConfigKeys.CUSTOM_PREFIX)) {
			newPrefix = AbstractMessage.formatColoredValue(this.get(ConfigKeys.CUSTOM_PREFIX_TEXT));
		} else {
			newPrefix = text()
					// [FC]
					.color(GRAY)
					.append(text('['))
					.append(text().decoration(BOLD, true).append(text('F', AQUA)).append(text('C', YELLOW)))
					.append(text(']'))
					.build();
		}
		try {
			Root.getTrusted().findStaticSetter(AbstractMessage.class, "PREFIX_COMPONENT", Component.class).invokeWithArguments(newPrefix);
		} catch (Throwable e) {
			throw TypeUtil.throwException(e);
		}
	}

}
