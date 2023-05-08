package org.floracore.api.event;

import org.bukkit.event.*;
import org.floracore.api.*;
import org.jetbrains.annotations.*;

public abstract class FloraCoreEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final FloraCore floraCore;
    private boolean cancelled = false;

    public FloraCoreEvent(FloraCore floraCore) {
        this.floraCore = floraCore;
    }

    public FloraCore getFloraCore() {
        return this.floraCore;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
