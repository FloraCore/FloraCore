/**
 * 数据库操作的接口
 */
package team.floracore.common.storage.implementation;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.sql.*;
import java.util.*;

public interface StorageImplementation {
    FloraCorePlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    void shutdown();


    Players selectPlayerBaseInfo(UUID uuid);

    Players selectPlayerBaseInfo(UUID uuid, String n, String loginIp);

    void deletePlayers(UUID u) throws SQLException;
}
