package team.floracore.bukkit.scoreboard.model;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

/**
 * 计分板模型
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 19:25
 */
public class BoardModel {
    public final transient String name;
    public final int index;
    @Nullable
    public final Date time_start;
    @Nullable
    public final Date time_end;
    public final String title;
    public final String permission;
    public final List<String> lines;

    public BoardModel(String name,
                      int index,
                      @Nullable Date timeStart,
                      @Nullable Date timeEnd,
                      String title,
                      String permission,
                      List<String> lines) {
        this.name = name;
        this.index = index;
        time_start = timeStart;
        time_end = timeEnd;
        this.title = title;
        this.permission = permission;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public List<String> getLines() {
        return lines;
    }

    public @Nullable Date getTime_end() {
        return time_end;
    }

    public @Nullable Date getTime_start() {
        return time_start;
    }

    public int getIndex() {
        return index;
    }

    public String getPermission() {
        return permission;
    }

    public String getTitle() {
        return title;
    }
}
