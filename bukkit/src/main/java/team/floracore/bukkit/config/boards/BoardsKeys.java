package team.floracore.bukkit.config.boards;

import team.floracore.bukkit.scoreboard.model.BoardModel;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.key.ConfigKey;
import team.floracore.common.config.generic.key.SimpleConfigKey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static team.floracore.common.config.generic.key.ConfigKeyFactory.*;

/**
 * All of the {@link ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class BoardsKeys {

    public static final ConfigKey<Boolean> ENABLE = notReloadable(booleanKey("enable", true));
    public static final ConfigKey<Integer> UPDATE_TIME = key(c -> c.getInteger("update-time", 10));
    public static final ConfigKey<List<String>> DISABLE_WORLDS = key(c -> c.getStringList("disable-worlds", new ArrayList<>()));

    public static final ConfigKey<List<BoardModel>> BOARD_MODELS = key(c -> {
        List<BoardModel> ret = new ArrayList<>();
        Set<String> boards = c.getKeys("boards", new HashSet<>());
        for (String board : boards) {
            int index = c.getInteger("boards." + board + ".index", Integer.MAX_VALUE);
            String title = c.getString("boards." + board + ".title", "null");
            String permission = c.getString("boards." + board + ".permission", null);
            List<String> lines = c.getStringList("boards." + board + ".lines", new ArrayList<>());
            String startTime = c.getString("boards." + board + ".time.start", null);
            String endTime = c.getString("boards." + board + ".time.end", null);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate;
            Date endDate;
            try {
                startDate = dateFormat.parse(startTime);
            } catch (Throwable e) {
                startDate = null;
            }
            try {
                endDate = dateFormat.parse(endTime);
            } catch (Throwable e) {
                endDate = null;
            }
            BoardModel boardModel = new BoardModel(board, index, startDate, endDate, title, permission, lines);
            ret.add(boardModel);
        }
        return ret;
    });

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(BoardsKeys.class);

    private BoardsKeys() {
    }

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}
