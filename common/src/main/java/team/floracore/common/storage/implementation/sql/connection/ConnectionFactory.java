package team.floracore.common.storage.implementation.sql.connection;

import team.floracore.common.plugin.FloraCorePlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public interface ConnectionFactory {

    String getImplementationName();

    void init(FloraCorePlugin plugin);

    void shutdown() throws Exception;

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;

}
