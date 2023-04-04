package team.floracore.common.config.generic.adapter;

import com.google.common.base.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

public abstract class StringBasedConfigurationAdapter implements ConfigurationAdapter {

    private static final Splitter LIST_SPLITTER = Splitter.on(',');
    private static final Splitter.MapSplitter MAP_SPLITTER = Splitter.on(',').withKeyValueSeparator('=');

    protected abstract @Nullable String resolveValue(String path);

    @Override
    public String getString(String path, String def) {
        String value = resolveValue(path);
        if (value == null) {
            return def;
        }

        return value;
    }

    @Override
    public int getInteger(String path, int def) {
        String value = resolveValue(path);
        if (value == null) {
            return def;
        }

        try {
            return Integer.parseInt(value);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        String value = resolveValue(path);
        if (value == null) {
            return def;
        }

        try {
            return Boolean.parseBoolean(value);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        String value = resolveValue(path);
        if (value == null) {
            return def;
        }

        return LIST_SPLITTER.splitToList(value);
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        String value = resolveValue(path);
        if (value == null) {
            return def;
        }

        return MAP_SPLITTER.split(value);
    }
}
