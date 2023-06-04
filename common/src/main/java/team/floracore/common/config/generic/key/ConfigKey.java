package team.floracore.common.config.generic.key;

import team.floracore.common.config.generic.adapter.ConfigurationAdapter;

/**
 * Represents a key in the configuration.
 *
 * @param <T> the value type
 */
public interface ConfigKey<T> {

	/**
	 * Gets the position of this key within the keys enum.
	 *
	 * @return the position
	 */
	int ordinal();

	/**
	 * Gets if the config key can be reloaded.
	 *
	 * @return the if the key can be reloaded
	 */
	boolean reloadable();

	/**
	 * Resolves and returns the value mapped to this key using the given config instance.
	 *
	 * @param adapter the config adapter instance
	 *
	 * @return the value mapped to this key
	 */
	T get(ConfigurationAdapter adapter);

}
