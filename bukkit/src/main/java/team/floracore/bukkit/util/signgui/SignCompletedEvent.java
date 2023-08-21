package team.floracore.bukkit.util.signgui;

import org.bukkit.entity.Player;

import java.util.List;

public final class SignCompletedEvent {
	private final Player player;
	private final List<String> lines;

	public SignCompletedEvent(Player player, List<String> lines) {
		this.player = player;
		this.lines = lines;
	}

	public List<String> getLines() {
		return lines;
	}

	public Player getPlayer() {
		return player;
	}
}