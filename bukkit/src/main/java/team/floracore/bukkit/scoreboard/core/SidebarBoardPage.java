package team.floracore.bukkit.scoreboard.core;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import team.floracore.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 侧边栏计分板页面
 *
 * @author xLikeWATCHDOG
 */
public class SidebarBoardPage extends BoardPage {
    private static final List<ChatColor> COLORS = Arrays.asList(ChatColor.values());
    private static int BOARD_LINE_MAX_CHARS = 16;
    private static int BOARD_LINE_MAX_CHARS_SUB1 = BOARD_LINE_MAX_CHARS - 1;
    private static boolean newVer = true;

    static {
        try {
            Team.class.getDeclaredMethod("addEntry", String.class);
        } catch (NoSuchMethodException e) {
            newVer = false;
        }
        try {
            Material.valueOf("KELP");
            BOARD_LINE_MAX_CHARS = 64;
            BOARD_LINE_MAX_CHARS_SUB1 = BOARD_LINE_MAX_CHARS - 1;
            Bukkit.getLogger().info("§a当前服务端支持新版记分板,长度限制为64个字符...");
        } catch (IllegalArgumentException ignored) {
        }
    }

    private final Objective objective;
    private final List<BoardLine> boardLines = new ArrayList<>();
    private int currentSize;

    public SidebarBoardPage() {
        super();
        objective = getBoard().registerNewObjective("default", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i = 0; i < COLORS.size(); i++) {
            final String name = COLORS.get(i) + String.valueOf(ChatColor.RESET);
            final Team team = getBoard().registerNewTeam("MiaoboardLine" + i);
            boardLines.add(new BoardLine(name, team));
        }
    }

    public Objective getObjective() {
        return objective;
    }

    public void setTitle(String title) {
        objective.setDisplayName(title);
    }

    public void setBody(List<String> newContents) {
        for (int i = 0; i < newContents.size(); i++) {
            setValue(newContents.size() - i, newContents.get(i));
        }
        clear(newContents.size());
    }

    public void setValue(int line, String value) {
        final BoardLine boardLine = getBoardLine(line);
        Validate.notNull(boardLine, "Unable to find BoardLine with index of " + line + ".");
        objective.getScore(boardLine.getName()).setScore(line);
        //分割字符串为前16个和后16个
        String prefix = value;
        String suffix = "";
        if (value.length() > BOARD_LINE_MAX_CHARS) {
            int splitIndex = value.charAt(BOARD_LINE_MAX_CHARS_SUB1) == ChatColor.COLOR_CHAR ?
                    BOARD_LINE_MAX_CHARS_SUB1 : BOARD_LINE_MAX_CHARS;
            prefix = StringUtil.substring(value, 0, splitIndex);
            suffix = value.substring(splitIndex);
            // 如果过suffix开头不是颜色符号就把prefix颜色转移到suffix
            if (suffix.charAt(0) != ChatColor.COLOR_CHAR) {
                suffix = ChatColor.getLastColors(prefix) + suffix;
            }
            if (suffix.length() > BOARD_LINE_MAX_CHARS) {
                suffix = StringUtil.substring(suffix, 0, BOARD_LINE_MAX_CHARS);
            }
        }
        boardLine.getTeam().setPrefix(prefix);
        boardLine.getTeam().setSuffix(suffix);
    }

    //all 5  [0 1 2 3 4] maxLine = 5  all 3 [0 1 2] maxLine=4
    public void clear(int size) {
        if (size < currentSize) {
            for (int i = size; i < currentSize; i++) {
                removeLine(i + 1);
            }
        }
        currentSize = size;
    }

    private BoardLine getBoardLine(int line) {
        return boardLines.get(line);
    }

    public void removeLine(int line) {
        final BoardLine boardLine = getBoardLine(line);
        Validate.notNull(boardLine, "Unable to find BoardLine with index of " + line + ".");
        getBoard().resetScores(boardLine.getName());
    }

    static class BoardLine {
        private final String name;
        private final Team team;
        private final OfflinePlayer player;

        public BoardLine(String name, Team team) {
            this.name = name;
            this.team = team;
            this.player = Bukkit.getOfflinePlayer(name);
            addEntry();
        }

        public void addEntry() {
            if (newVer) {
                team.addEntry(name);
            } else {
                team.addPlayer(player);
            }
        }

        public String getName() {
            return name;
        }

        public Team getTeam() {
            return team;
        }
    }
}
