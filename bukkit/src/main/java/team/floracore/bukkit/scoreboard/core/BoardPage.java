package team.floracore.bukkit.scoreboard.core;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

/**
 * 计分板页面
 *
 * @author xLikeWATCHDOG
 */
public abstract class BoardPage {
    private final Scoreboard board;

    public BoardPage() {
        board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
    }

    public Scoreboard getBoard() {
        return board;
    }
}
