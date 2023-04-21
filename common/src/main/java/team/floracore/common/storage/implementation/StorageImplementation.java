/**
 * 数据库操作的接口
 */
package team.floracore.common.storage.implementation;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public interface StorageImplementation {
    FloraCorePlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    void shutdown();

    /**
     * 此方法慎用，未经过缓存，频繁使用易使负载过大。
     */
    Players selectPlayers(String name);

    Players selectPlayers(UUID uuid);

    Players selectPlayers(UUID uuid, String n, String loginIp);

    void deletePlayers(UUID u);

    /**
     * 虽然名称是插入数据，但是并不是简单的插入数据。
     * 如果不存在数据，才会进行插入操作。
     * 如果存在数据，则会读取数据，而不进行插入操作。
     * 如果存在数据，还会同时更新value和expiry数据。
     *
     * @return Data数据。
     */
    Data insertData(UUID uuid, String type, String key, String value, long expiry);

    List<Data> selectData(UUID uuid);

    Data getSpecifiedData(UUID uuid, String type, String key);

    void deleteDataAll(UUID uuid);

    void deleteDataExpired(UUID uuid);

    void deleteDataID(int id);
}
