package team.floracore.common.config.generic;

import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.config.generic.key.*;
import team.floracore.common.util.*;

import java.lang.reflect.*;
import java.util.*;

public class KeyedConfiguration {
    private final ConfigurationAdapter adapter;
    private final List<? extends ConfigKey<?>> keys;
    private final ValuesMap values;

    public KeyedConfiguration(ConfigurationAdapter adapter, List<? extends ConfigKey<?>> keys) {
        this.adapter = adapter;
        this.keys = keys;
        this.values = new ValuesMap(keys.size());
    }

    /**
     * Initialises the given pseudo-enum keys class.
     *
     * @param keysClass the keys class
     * @return the list of keys defined by the class with their ordinal values set
     */
    public static List<SimpleConfigKey<?>> initialise(Class<?> keysClass) {
        // get a list of all keys
        List<SimpleConfigKey<?>> keys = Arrays.stream(keysClass.getFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> ConfigKey.class.equals(f.getType()))
                .map(f -> {
                    try {
                        return f.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(ImmutableCollectors.toList());

        // set ordinal values
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).setOrdinal(i);
        }

        return keys;
    }

    protected void init() {
        load(true);
    }

    protected void load(boolean initial) {
        for (ConfigKey<?> key : this.keys) {
            if (initial || key.reloadable()) {
                this.values.put(key, key.get(this.adapter));
            }
        }
    }

    /**
     * Gets the value of a given context key.
     *
     * @param key the key
     * @param <T> the key return type
     * @return the value mapped to the given key. May be null.
     */
    public <T> T get(ConfigKey<T> key) {
        return this.values.get(key);
    }

    /**
     * Reloads the configuration.
     */
    public void reload() {
        this.adapter.reload();
        load(false);
    }

    public static class ValuesMap {
        private final Object[] values;

        public ValuesMap(int size) {
            this.values = new Object[size];
        }

        @SuppressWarnings("unchecked")
        public <T> T get(ConfigKey<T> key) {
            return (T) this.values[key.ordinal()];
        }

        public void put(ConfigKey<?> key, Object value) {
            this.values[key.ordinal()] = value;
        }
    }
}
