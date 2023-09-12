package team.floracore.common.config.generic.adapter;

import team.floracore.common.plugin.FloraCorePlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigurationAdapter {

	FloraCorePlugin getPlugin();

	void reload();

	String getString(String path, String def);

	int getInteger(String path, int def);

	float getFloat(String path, float def);

	double getDouble(String path, double def);

	boolean getBoolean(String path, boolean def);

	long getLong(String path, long def);

	List<String> getStringList(String path, List<String> def);

	Map<String, String> getStringMap(String path, Map<String, String> def);

	Map<String, Boolean> getBooleanMap(String path, Map<String, Boolean> def);

	Map<String, Integer> getIntegerMap(String path, Map<String, Integer> def);

	Map<String, Double> getDoubleMap(String path, Map<String, Double> def);

	Set<String> getKeys(String path, Set<String> def);

	Collection<String> getKeys();

	void set(String path, Object value);
}
