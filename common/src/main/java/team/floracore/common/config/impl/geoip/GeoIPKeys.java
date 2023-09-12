package team.floracore.common.config.impl.geoip;

import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.key.ConfigKey;
import team.floracore.common.config.generic.key.SimpleConfigKey;

import java.util.List;

import static team.floracore.common.config.generic.key.ConfigKeyFactory.*;

public class GeoIPKeys {
	public static final ConfigKey<Boolean> ENABLE = booleanKey("enable", true);
	public static final ConfigKey<Boolean> DATABASE_SHOW_CITIES = booleanKey("database.show-cities", false);
	public static final ConfigKey<String> DATABASE_DOWNLOAD_URL_CITY = stringKey("database.download-url-city", null);
	public static final ConfigKey<String> DATABASE_DOWNLOAD_URL = stringKey("database.download-url", null);
	public static final ConfigKey<String> DATABASE_LICENSE_KEY = stringKey("database.license-key", "");
	public static final ConfigKey<Boolean> ENABLE_LOCALE = booleanKey("enable-locale", false);
	public static final ConfigKey<Boolean> DATABASE_DOWNLOAD_IF_MISSING = booleanKey("database.download-if-missing", true);
	public static final ConfigKey<Boolean> DATABASE_UPDATE_ENABLE = booleanKey("database.update.enable", true);
	public static final ConfigKey<Long> DATABASE_UPDATE_BY_EVERY_X_DAYS = longKey("database.update.by-every-x-days", 30);

	/**
	 * A list of the keys defined in this class.
	 */
	private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(GeoIPKeys.class);

	private GeoIPKeys() {
	}

	public static List<? extends ConfigKey<?>> getKeys() {
		return KEYS;
	}
}
