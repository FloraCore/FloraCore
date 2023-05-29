package team.floracore.bukkit.scoreboard.core;

import org.bukkit.*;
import org.bukkit.scoreboard.*;

/**
 * 计分板页面
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 19:36
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
