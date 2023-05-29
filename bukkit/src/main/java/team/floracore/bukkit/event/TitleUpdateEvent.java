package team.floracore.bukkit.event;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

/**
 * 记分板标题更新
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 19:33
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
