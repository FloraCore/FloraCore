package team.floracore.bukkit.util.event;

import org.bukkit.event.*;
import team.floracore.bukkit.util.module.*;
import team.floracore.common.util.*;

import java.util.*;

/**
 * @see IModule
 */
public class ModuleDisableEvent extends Event implements IFutureEvent, Cancellable {
    public static HandlerList handlers = new HandlerList();
    public final IModule module;
    public boolean cancelled = false;
    public List<TypeUtil.Runnable> tasks = new LinkedList<>();

    public ModuleDisableEvent(IModule module) {
        this.module = module;
    }

    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<TypeUtil.Runnable> getTasks() {
        return tasks;
    }
}
