package team.floracore.common.storage.implementation;

import org.floracore.api.data.DataType;
import org.floracore.api.data.chat.ChatType;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.sql.connection.ConnectionFactory;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

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

    /**
     * 对参数"parameters"有以下解释，
     * 1.该参数内容由参数"type"决定。
     * 2.若type为{@link ChatType#SERVER},则该参数内容为服务器名。
     * 3.若type为{@link ChatType#PARTY},则该参数内容为组队UUID。
     */
    void insertChat(ChatType type, String parameters, UUID uuid, String message, long time);


    List<REPORT> getReports();

    List<REPORT> selectReports(UUID uuid);

    REPORT selectReport(UUID uuid);

    /**
     * @return 返回未处理的举报；若无,则返回null。
     */
    REPORT getUnprocessedReports(UUID uuid);

    void addReport(UUID uuid,
                   UUID reporter,
                   UUID reported,
                   String reason,
                   long reportTime);

    List<CHAT> selectChat(UUID uuid, ChatType chatType);
}
