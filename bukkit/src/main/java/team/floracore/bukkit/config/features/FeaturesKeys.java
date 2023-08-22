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
		final double maxSpeed = c.getDouble("speed.max-fly-speed", 0.8);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	});

	public static final ConfigKey<Double> SPEED_MAX_WALK_SPEED = key(c -> {
		final double maxSpeed = c.getDouble("speed.max-walk-speed", 0.8);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	});

	public static final ConfigKey<Boolean> TELEPORT_SAFETY = booleanKey("teleport-safety", true);

	public static final ConfigKey<String> JOIN_MESSAGE = stringKey("join-message.message", "&a[+] %player%");

	public static final ConfigKey<Boolean> JOIN_MESSAGE_ENABLE = booleanKey("join-message.enable", true);

	public static final ConfigKey<String> QUIT_MESSAGE = stringKey("quit-message.message", "&c[-] %player%");

	public static final ConfigKey<Boolean> QUIT_MESSAGE_ENABLE = booleanKey("quit-message.enable", true);

	public static final ConfigKey<Boolean> NO_WEATHER = booleanKey("no-weather", true);

	public static final ConfigKey<Boolean> NO_FALL = booleanKey("no-fall", true);

	public static final ConfigKey<Boolean> NO_HUNGRY = booleanKey("no-hungry", true);

	public static final ConfigKey<Boolean> NO_FIRE_DAMAGE = booleanKey("no-fire-damage", true);

	public static final ConfigKey<String> DEATH_MESSAGE = stringKey("death-message.message", "%player% dead!");

	public static final ConfigKey<Boolean> DEATH_MESSAGE_ENABLE = booleanKey("death-message.enable", false);

	public static final ConfigKey<Boolean> MOB_SPAWN = booleanKey("mob-spawn", false);
	public static final ConfigKey<Boolean> PLACE_BLOCK = booleanKey("place-block", false);
	public static final ConfigKey<Boolean> BREAK_BLOCK = booleanKey("break-block", false);

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
