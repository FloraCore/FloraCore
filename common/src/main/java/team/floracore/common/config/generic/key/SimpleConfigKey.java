package team.floracore.common.config.generic.key;

import team.floracore.common.config.generic.adapter.*;

import java.util.function.*;

/**
 * Basic {@link ConfigKey} implementation.
 *
 * @param <T> the value type
 */
public class SimpleConfigKey<T> implements ConfigKey<T> {
    private final Function<? super ConfigurationAdapter, ? extends T> function;

    private int ordinal = -1;
    private boolean reloadable = true;

    SimpleConfigKey(Function<? super ConfigurationAdapter, ? extends T> function) {
        this.function = function;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public boolean reloadable() {
        return this.reloadable;
    }

    @Override
    public T get(ConfigurationAdapter adapter) {
        return this.function.apply(adapter);
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }
}
