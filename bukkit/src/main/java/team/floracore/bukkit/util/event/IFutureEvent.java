package team.floracore.bukkit.util.event;

import org.bukkit.Bukkit;
import team.floracore.common.util.TypeUtil;

import java.util.List;

public interface IFutureEvent {
	default void doAfter(TypeUtil.Runnable task) {
		getTasks().add(task);
	}

	List<TypeUtil.Runnable> getTasks();

	/**
	 * Triggered after the event occurs or is cancelled
	 */
	default void done() {
		for (TypeUtil.Runnable task : getTasks()) {
			try {
				task.run();
			} catch (Throwable e) {
				Bukkit.getLogger().warning("Error on task " + task.getClass().getName());
				e.printStackTrace();
			}
		}
	}
}
