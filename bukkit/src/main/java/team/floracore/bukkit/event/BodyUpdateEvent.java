package team.floracore.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * 记分板更新事件
 *
 * @author xLikeWATCHDOG
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
