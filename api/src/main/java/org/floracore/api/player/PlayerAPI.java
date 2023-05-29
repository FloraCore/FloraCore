package org.floracore.api.player;

import java.util.*;

/**
 * 玩家API。
 */
public interface PlayerAPI {
    /**
     * 获取服务器内是否有这名玩家的记录。
     * 判断逻辑是通过name查询这名玩家是否上线过。
     * 可能出现的问题是,无法判定这名玩家之前的昵称。
     *
     * @param name 玩家昵称
     * @return 是否记录状况
     */
    boolean hasPlayerRecord(String name);

    /**
     * 获取服务器内这名玩家记录的UUID值。
     * 该值通常情况下不会变。
     * 若不存在,则返回null。
     *
     * @param name 玩家昵称
     * @return 玩家UUID
     */
    UUID getPlayerRecordUUID(String name);

    /**
     * 通过数据库获取玩家最后记录的名字。
     *
     * @param uuid 玩家的UUID
     * @return 最后记录的名字, 若无返回null
     */
    String getPlayerRecordName(UUID uuid);

    /**
     * 判断玩家是否在线。
     * 该逻辑首先判断玩家是否在当前服务器中在线。
     * 如果不在,再通过数据库获取。
     *
     * @param uuid 玩家的UUID
     * @return 在线情况
     */
    boolean isOnline(UUID uuid);
}
