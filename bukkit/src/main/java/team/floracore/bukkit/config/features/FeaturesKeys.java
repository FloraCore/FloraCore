package team.floracore.bukkit.config.features;

import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.key.ConfigKey;
import team.floracore.common.config.generic.key.SimpleConfigKey;

import java.util.List;

import static team.floracore.common.config.generic.key.ConfigKeyFactory.*;

/**
 * All of the {@link team.floracore.common.config.generic.key.ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class FeaturesKeys {
	public static final ConfigKey<Double> SPEED_MAX_FLY_SPEED = key(c -> {
		final double maxSpeed = c.getDouble("commands.speed.max-fly-speed", 0.8);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	});

	public static final ConfigKey<Double> SPEED_MAX_WALK_SPEED = key(c -> {
		final double maxSpeed = c.getDouble("commands.speed.max-walk-speed", 0.8);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	});
	/**
	 * A list of the keys defined in this class.
	 */
	private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(FeaturesKeys.class);

	private FeaturesKeys() {
	}

	public static List<? extends ConfigKey<?>> getKeys() {
		return KEYS;
	}
}
