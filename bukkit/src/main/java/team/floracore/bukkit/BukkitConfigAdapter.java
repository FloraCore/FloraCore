package team.floracore.bukkit;

import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.plugin.*;

import java.io.*;
import java.util.*;

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
    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
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
        return Float.parseFloat(this.configuration.get(path, def).toString());
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
    public FloraCorePlugin getPlugin() {
        return this.plugin;
    }
}
