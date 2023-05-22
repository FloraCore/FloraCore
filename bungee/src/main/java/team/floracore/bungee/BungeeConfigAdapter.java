package team.floracore.bungee;

import net.md_5.bungee.config.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.plugin.*;

import java.io.*;
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
            map.put(key, section.get(key).toString());
        }

        return map;
    }

    @Override
    public FloraCorePlugin getPlugin() {
        return this.plugin;
    }
}