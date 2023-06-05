package team.floracore.common.util;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.concurrent.TimeUnit;

/**
 * A simple expiring set implementation using Caffeine caches
 *
 * @param <E> element type
 */
public class ExpiringSet<E> {
    private final Cache<E, Long> cache;
    private final long lifetime;

    public ExpiringSet(long duration, TimeUnit unit) {
        this.cache = CaffeineFactory.newBuilder().expireAfterWrite(duration, unit).build();
        this.lifetime = unit.toMillis(duration);
    }

    public boolean add(E item) {
        boolean present = contains(item);
        this.cache.put(item, System.currentTimeMillis() + this.lifetime);
        return !present;
    }

    public boolean contains(E item) {
        Long timeout = this.cache.getIfPresent(item);
        return timeout != null && timeout > System.currentTimeMillis();
    }

    public void remove(E item) {
        this.cache.invalidate(item);
    }
}
