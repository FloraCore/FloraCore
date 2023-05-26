package team.floracore.common.util;

import com.github.benmanes.caffeine.cache.*;

import java.util.concurrent.*;

public final class CaffeineFactory {
    /**
     * Our own fork joins pool for FloraCore cache operations.
     * <p>
     * By default, Caffeine uses the ForkJoinPool.commonPool instance.
     * However... ForkJoinPool is a fixed size pool limited by Runtime.availableProcessors.
     * Some (bad) plugins incorrectly use this pool for i/o operations, make calls to Thread.sleep
     * or otherwise block waiting for something else to complete. This prevents the FC cache loading
     * operations from running.
     * <p>
     * By using our own pool, we ensure this will never happen.
     */
    private static final ForkJoinPool loaderPool = new ForkJoinPool();

    private CaffeineFactory() {
    }

    public static Caffeine<Object, Object> newBuilder() {
        return Caffeine.newBuilder().executor(loaderPool);
    }

    public static Executor executor() {
        return loaderPool;
    }

}
