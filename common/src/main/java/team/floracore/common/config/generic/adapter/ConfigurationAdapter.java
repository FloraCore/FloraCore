package team.floracore.common.config.generic.adapter;

import team.floracore.common.plugin.*;

import java.util.*;

public interface ConfigurationAdapter {

    FloraCorePlugin getPlugin();

    void reload();

    String getString(String path, String def);

    int getInteger(String path, int def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    Map<String, String> getStringMap(String path, Map<String, String> def);

}