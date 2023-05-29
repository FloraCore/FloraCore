package team.floracore.bukkit.scoreboard;

import org.bukkit.*;
import org.bukkit.entity.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.config.*;
import team.floracore.bukkit.scoreboard.core.*;
import team.floracore.bukkit.scoreboard.model.*;

import java.util.*;

/**
 * 计分板管理器
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 19:30
 */
public class ScoreBoardManager {
    private final Status cot = new Status();
    private final SidebarBoard sbd = new SidebarBoard(FCBukkitBootstrap.loader);
    private final List<BoardModel> bms = new LinkedList<>();
    private final FCBukkitPlugin plugin;

    public ScoreBoardManager(FCBukkitPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        bms.clear();
        List<BoardModel> i = plugin.getBoardsConfiguration().get(BoardsKeys.BOARD_MODELS);
        bms.addAll(i);
        bms.sort(Comparator.comparing(o -> o.index));
    }

    public List<BoardModel> getModels() {
        return bms;
    }

    public void reload() {
        sbd.cancel();
        plugin.getBoardsConfiguration().reload();
        load();
        start();
    }

    public void start() {
        sbd.update(cot.set(true), plugin.getBoardsConfiguration().get(BoardsKeys.UPDATE_TIME));
        Bukkit.getOnlinePlayers().forEach(this::addTarget);
    }

    public void addTarget(final Player player) {
        if (!plugin.getBoardsConfiguration().get(BoardsKeys.DISABLE_WORLDS).contains(player.getWorld().getName())) {
            Bukkit.getScheduler().runTask(plugin.getLoader(), () -> getSidebarBoard().addTarget(player));
        }
    }

    public SidebarBoard getSidebarBoard() {
        return sbd;
    }

    public void removeTarget(final Player player) {
        sbd.removeTarget(player);
    }

    private static class Status implements Condition {
        private boolean status = true;

        @Override
        public boolean get() {
            return status;
        }

        Status set(boolean status) {
            this.status = status;
            return this;
        }
    }
}
