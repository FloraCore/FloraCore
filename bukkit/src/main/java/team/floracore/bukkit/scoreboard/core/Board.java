package team.floracore.bukkit.scoreboard.core;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 计分板
 *
 * @author xLikeWATCHDOG
 */

public abstract class Board implements Iterable<Player> {
	@Getter
	private final Plugin plugin;
	private final HashMap<Player, BoardPage> targets = new HashMap<>();
	private final Set<Player> removeQueue = new HashSet<>();
	private int taskId;

	public Board(final Plugin plugin) {
		this.plugin = plugin;
	}

	public boolean addTarget(final Player player) {
		if (!this.isTarget(player)) {
			final BoardPage boardPage = this.newPage();
			this.targets.put(player, boardPage);
			player.setScoreboard(boardPage.getBoard());
			this.update(player);
			return true;
		}
		return false;
	}

	public boolean isTarget(final Player player) {
		return this.targets.containsKey(player);
	}

	public abstract BoardPage newPage();

	public abstract void update(Player p);

	public BoardPage getBoardPage(final Player player) {
		return this.targets.get(player);
	}

	public Set<Player> getTargets() {
		return targets.keySet();
	}

	public boolean isRunning() {
		return this.taskId != 0;
	}

	public boolean removeTarget(final Player player) {
		if (this.isTarget(player)) {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			return this.removeQueue.add(player);
		}
		return false;
	}

	public void update(final Condition condition, final int interval) {
		taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			if (condition.get()) {
				final Iterator<Player> iterator = iterator();
				while (iterator.hasNext()) {
					final Player next = iterator.next();
					if (shouldRemove(next)) {
						iterator.remove();
					} else {
						update(next);
					}
				}
			} else {
				cancel();
			}
		}, 0, interval).getTaskId();
	}

	@NotNull
	@Override
	public Iterator<Player> iterator() {
		return this.targets.keySet().iterator();
	}

	private boolean shouldRemove(final Player player) {
		return this.removeQueue.remove(player);
	}

	public void cancel() {
		if (taskId != 0) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = 0;
			for (final Player player : this.targets.keySet()) {
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
			this.targets.clear();
			this.removeQueue.clear();
		}
	}

}
