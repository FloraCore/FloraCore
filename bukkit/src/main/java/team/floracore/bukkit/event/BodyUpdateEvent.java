package team.floracore.bukkit.event;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * 记分板更新事件
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 19:32
 */
public class BodyUpdateEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private List<String> body = Collections.emptyList();

    public BodyUpdateEvent(Player player) {
        super(true);
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
