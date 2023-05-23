package team.floracore.bukkit.util.event;

import team.floracore.common.util.*;

import java.util.*;

public interface IFutureEvent {
    List<TypeUtil.Runnable> getTasks();

    default void doAfter(TypeUtil.Runnable task) {
        getTasks().add(task);
    }

    /**
     * Triggered after the event occurs or is cancelled
     */
    default void done() {
        for (TypeUtil.Runnable task : getTasks()) {
            try {
                task.run();
            } catch (Throwable e) {
                System.out.println("Err on task " + task.getClass().getName());
                e.printStackTrace();
            }
        }
    }
}
