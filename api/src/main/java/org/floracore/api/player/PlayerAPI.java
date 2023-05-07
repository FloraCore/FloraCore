package org.floracore.api.player;

import java.util.*;

/**
 * 玩家API。
 */
public interface PlayerAPI {

    /**
     * 通过数据库获取玩家最后记录的名字。
     *
     * @param uuid 玩家的UUID
     * @return 最后记录的名字
     */
    String getPlayerRecordName(UUID uuid);

    boolean isOnline(UUID uuid);
}
