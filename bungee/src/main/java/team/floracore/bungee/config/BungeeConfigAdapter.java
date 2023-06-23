package team.floracore.bungee.config;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import team.floracore.common.config.generic.adapter.ConfigurationAdapter;
import team.floracore.common.plugin.FloraCorePlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BungeeConfigAdapter implements ConfigurationAdapter {
    private final FloraCorePlugin plugin;
    private final File file;
    private Configuration configuration;

    public BungeeConfigAdapter(FloraCorePlugin plugin, File file) {
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
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getString(String path, String def) {
        return this.configuration.getString(path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return this.configuration.getInt(path, def);
    }

    @Override
    public float getFloat(String path, float def) {
        return this.configuration.getFloat(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return this.configuration.getDouble(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.configuration.getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        return Optional.of(this.configuration.getStringList(path)).orElse(def);
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        Map<String, String> map = new HashMap<>();
        Configuration section = this.configuration.getSection(path);
        if (section == null) {
            return def;
        }

        for (String key : section.getKeys()) {
            map.put(key, section.getString(key));
        }
        return map;
    }

    @Override
    public Map<String, Boolean> getBooleanMap(String path, Map<String, Boolean> def) {
        Map<String, Boolean> map = new HashMap<>();
        Configuration section = this.configuration.getSection(path);
        if (section == null) {
            return def;
        }

        for (String key : section.getKeys()) {
            map.put(key, section.getBoolean(key));
        }
        return map;
    }

    @Override
    public Map<String, Integer> getIntegerMap(String path, Map<String, Integer> def) {
        Map<String, Integer> map = new HashMap<>();
        Configuration section = this.configuration.getSection(path);
        if (section == null) {
            return def;
        }

        for (String key : section.getKeys()) {
            map.put(key, section.getInt(key));
        }
        return map;
    }

    @Override
    public Map<String, Double> getDoubleMap(String path, Map<String, Double> def) {
        Map<String, Double> map = new HashMap<>();
        Configuration section = this.configuration.getSection(path);
        if (section == null) {
            return def;
        }

        for (String key : section.getKeys()) {
            map.put(key, section.getDouble(key));
        }
        return map;
    }

    @Override
    public Collection<String> getKeys() {
        return this.configuration.getKeys();
    }

    @Override
    public void set(String path, Object value) {
        this.configuration.set(path, value);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}