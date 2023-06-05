package team.floracore.bukkit.scoreboard.model;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Date;
import java.util.List;

/**
 * 计分板模型
 *
 * @author xLikeWATCHDOG
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
}
