package team.floracore.common.storage.implementation;

import org.floracore.api.data.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.sql.connection.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.function.*;

/**
 * 数据库操作的接口
 */
public interface StorageImplementation {
    FloraCorePlugin getPlugin();

    String getImplementationName();

    ConnectionFactory getConnectionFactory();

    Function<String, String> getStatementProcessor();

    void init() throws Exception;

    void shutdown();

    /**
     * 此方法慎用,未经过缓存,频繁使用易使负载过大。
     * 若不存在,则返回null。
     */
    PLAYER selectPlayer(String name);

    PLAYER selectPlayer(UUID uuid);

    void deletePlayer(UUID u);

    /**
     * 虽然名称是插入数据,但是并不是简单的插入数据。
     * 如果不存在数据,才会进行插入操作。
     * 如果存在数据,则会读取数据,而不进行插入操作。
     * 如果存在数据,还会同时更新value和expiry数据。
     *
     * @return Data数据。
     */
    DATA insertData(UUID uuid, DataType type, String key, String value, long expiry);

    List<DATA> selectData(UUID uuid);

    /**
     * 如果无该记录,则返回Null。
     */
    DATA getSpecifiedData(UUID uuid, DataType type, String key);

    List<DATA> getSpecifiedTypeData(UUID uuid, DataType type);

    void deleteDataAll(UUID uuid);

    void deleteDataType(UUID uuid, DataType type);

    void deleteDataExpired(UUID uuid);

    void deleteDataID(int id);

    SERVER selectServer(String name);

    PARTY selectParty(UUID uuid);

    PARTY selectEffectiveParty(UUID uuid);

    void insertParty(UUID uuid, UUID leader, long createTime);

    ONLINE selectOnline(UUID uuid);

    void insertOnline(UUID uuid, boolean status, String serverName);
}
