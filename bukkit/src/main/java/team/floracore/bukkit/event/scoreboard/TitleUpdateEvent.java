package team.floracore.bukkit.event.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * 记分板标题更新
 *
 * @author xLikeWATCHDOG
 */
public class TitleUpdateEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private String title;

    public TitleUpdateEvent(Player player) {
        super(true);
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
