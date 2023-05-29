package team.floracore.bukkit.scoreboard.core;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * 计分板
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 19:37
 */

public abstract class Board implements Iterable<Player> {
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

    public Plugin getPlugin() {
        return plugin;
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
        taskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
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
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = 0;
            for (final Player player : this.targets.keySet()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
            this.targets.clear();
            this.removeQueue.clear();
        }
    }

}