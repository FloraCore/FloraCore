package team.floracore.bukkit.scoreboard.core;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import team.floracore.bukkit.event.*;

/**
 * 侧边栏计分板
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 19:38
 */
public class SidebarBoard extends Board {
    public SidebarBoard(final Plugin plugin) {
        super(plugin);
    }

    @Override
    public SidebarBoardPage newPage() {
        return new SidebarBoardPage();
    }

    @Override
    public void update(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            final SidebarBoardPage boardPage = this.getBoardPage(player);
            if (boardPage == null) {
                return;
            }
            TitleUpdateEvent te = new TitleUpdateEvent(player);
            Bukkit.getPluginManager().callEvent(te);
            String title = te.getTitle();
            if (title == null) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                return;
            }
            boardPage.setTitle(title);
            BodyUpdateEvent be = new BodyUpdateEvent(player);
            Bukkit.getPluginManager().callEvent(be);
            boardPage.setBody(be.getBody());
            player.setScoreboard(boardPage.getBoard());
        });
    }

    @Override
    public SidebarBoardPage getBoardPage(final Player player) {
        return (SidebarBoardPage) super.getBoardPage(player);
    }
}
