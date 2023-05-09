package team.floracore.common.storage.implementation;

import org.floracore.api.commands.report.*;
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
     * 此方法慎用，未经过缓存，频繁使用易使负载过大。
     * 若不存在，则返回null。
     */
    Players selectPlayers(String name);

    Players selectPlayers(UUID uuid);

    void deletePlayers(UUID u);

    /**
     * 虽然名称是插入数据，但是并不是简单的插入数据。
     * 如果不存在数据，才会进行插入操作。
     * 如果存在数据，则会读取数据，而不进行插入操作。
     * 如果存在数据，还会同时更新value和expiry数据。
     *
     * @return Data数据。
     */
    Data insertData(UUID uuid, DataType type, String key, String value, long expiry);

    List<Data> selectData(UUID uuid);

    /**
     * 如果无该记录，则返回Null。
     */
    Data getSpecifiedData(UUID uuid, DataType type, String key);

    List<Data> getSpecifiedTypeData(UUID uuid, DataType type);

    void deleteDataAll(UUID uuid);

    void deleteDataType(UUID uuid, DataType type);

    void deleteDataExpired(UUID uuid);

    void deleteDataID(int id);

    Servers selectServers(String name);

    List<Chat> selectChat(String name);

    Chat selectChatWithStartTime(String name, long startTime);

    void insertChat(String name, long startTime);

    List<Report> getReports();

    List<Report> selectReports(UUID uuid);

    /**
     * @return 返回未处理的举报；若无，则返回null。
     */
    Report getUnprocessedReports(UUID uuid);

    void addReport(UUID uuid, UUID reporter, UUID reported, String reason, long reportTime, List<ReportDataChatRecord> chat);
}
