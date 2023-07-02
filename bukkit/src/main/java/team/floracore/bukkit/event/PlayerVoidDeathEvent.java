package team.floracore.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author xLikeWATCHDOG
 */
public class PlayerVoidDeathEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private boolean cancel = false;

    public PlayerVoidDeathEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
