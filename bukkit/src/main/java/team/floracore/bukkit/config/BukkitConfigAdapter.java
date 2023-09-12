package team.floracore.bukkit.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.plugin.FloraCorePlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BukkitConfigAdapter implements ConfigurationAdapter {
	private final FloraCorePlugin plugin;
	private final File file;
	private YamlConfiguration configuration;

	public BukkitConfigAdapter(FloraCorePlugin plugin, File file) {
		this.plugin = plugin;
		this.file = file;
		reload();
	}

	@Override
	public FloraCorePlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public void reload() {
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
	}

	@Override
	public String getString(String path, String def) {
		return this.configuration.getString(path, def);
	}

	@Override
	public double getDouble(String path, double def) {
		return this.configuration.getDouble(path, def);
	}

	@Override
	public int getInteger(String path, int def) {
		return this.configuration.getInt(path, def);
	}

	@Override
	public float getFloat(String path, float def) {
		return Float.parseFloat(this.configuration.get(path, def).toString());
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return this.configuration.getBoolean(path, def);
	}

	@Override
	public long getLong(String path, long def) {
		return this.configuration.getLong(path, def);
	}

	@Override
	public List<String> getStringList(String path, List<String> def) {
		List<String> list = this.configuration.getStringList(path);
		return this.configuration.isSet(path) ? list : def;
	}

	@Override
	public Map<String, String> getStringMap(String path, Map<String, String> def) {
		Map<String, String> map = new HashMap<>();
		ConfigurationSection section = this.configuration.getConfigurationSection(path);
		if (section == null) {
			return def;
		}
		for (String key : section.getKeys(false)) {
			map.put(key, section.getString(key));
		}
		return map;
	}

	@Override
	public Map<String, Boolean> getBooleanMap(String path, Map<String, Boolean> def) {
		Map<String, Boolean> map = new HashMap<>();
		ConfigurationSection section = this.configuration.getConfigurationSection(path);
		if (section == null) {
			return def;
		}
		for (String key : section.getKeys(false)) {
			map.put(key, section.getBoolean(key));
		}
		return map;
	}

	@Override
	public Map<String, Integer> getIntegerMap(String path, Map<String, Integer> def) {
		Map<String, Integer> map = new HashMap<>();
		ConfigurationSection section = this.configuration.getConfigurationSection(path);
		if (section == null) {
			return def;
		}
		for (String key : section.getKeys(false)) {
			map.put(key, section.getInt(key));
		}
		return map;
	}

	@Override
	public Map<String, Double> getDoubleMap(String path, Map<String, Double> def) {
		Map<String, Double> map = new HashMap<>();
		ConfigurationSection section = this.configuration.getConfigurationSection(path);
		if (section == null) {
			return def;
		}
		for (String key : section.getKeys(false)) {
			map.put(key, section.getDouble(key));
		}
		return map;
	}

	@Override
	public Set<String> getKeys(String path, Set<String> def) {
		ConfigurationSection section = this.configuration.getConfigurationSection(path);
		if (section == null) {
			return def;
		}
		return new HashSet<>(section.getKeys(false));
	}

	@Override
	public Collection<String> getKeys() {
		return this.configuration.getKeys(false);
	}

	@Override
	public void set(String path, Object value) {
		this.configuration.set(path, value);
		try {
			this.configuration.save(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
