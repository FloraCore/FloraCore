package team.floracore.bukkit.util.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import team.floracore.bukkit.util.module.IModule;
import team.floracore.common.util.TypeUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @see IModule
 */
public class ModuleEnableEvent extends Event implements IFutureEvent, Cancellable {
    public static HandlerList handlers = new HandlerList();
    public final IModule module;
    public boolean cancelled = false;
    public List<TypeUtil.Runnable> tasks = new LinkedList<>();

    public ModuleEnableEvent(IModule module) {
        this.module = module;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return getHandlerList();
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
