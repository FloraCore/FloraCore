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


    Players selectPlayers(UUID uuid);

    Players selectPlayers(UUID uuid, String n, String loginIp);

    void deletePlayers(UUID u);

    Data insertData(UUID uuid, String type, String key, String value, long expiry);

    List<Data> selectData(UUID uuid);

    Data getSpecifiedData(UUID uuid, String type, String key);

    void deleteDataAll(UUID uuid);

    void deleteDataExpired(UUID uuid);

    void deleteDataType(UUID uuid, String type);

    void deleteDataKey(UUID uuid, String type, String key);
}
