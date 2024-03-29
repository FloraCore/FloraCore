package team.floracore.common.storage;

import lombok.Getter;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.util.Throwing;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Provides a {@link java.util.concurrent.CompletableFuture} based API for interacting with a
 * {@link StorageImplementation}.
 */
public class Storage {
	private final FloraCorePlugin plugin;
	@Getter
	private final StorageImplementation implementation;

	public Storage(FloraCorePlugin plugin, StorageImplementation implementation) {
		this.plugin = plugin;
		this.implementation = implementation;
	}

	public Collection<StorageImplementation> getImplementations() {
		return Collections.singleton(this.implementation);
	}

	private <T> CompletableFuture<T> future(Callable<T> supplier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return supplier.call();
			} catch (Exception e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new CompletionException(e);
			}
		}, this.plugin.getBootstrap().getScheduler().async());
	}

	private CompletableFuture<Void> future(Throwing.Runnable runnable) {
		return CompletableFuture.runAsync(() -> {
			try {
				runnable.run();
			} catch (Exception e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new CompletionException(e);
			}
		}, this.plugin.getBootstrap().getScheduler().async());
	}

	public String getName() {
		return this.implementation.getImplementationName();
	}

	public void init() {
		try {
			this.implementation.init();
		} catch (Exception e) {
			this.plugin.getLogger().severe("Failed to init storage implementation", e);
		}
	}

	public void shutdown() {
		try {
			this.implementation.shutdown();
		} catch (Exception e) {
			this.plugin.getLogger().severe("Failed to shutdown storage implementation", e);
		}
	}
}
