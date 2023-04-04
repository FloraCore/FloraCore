package team.floracore.common.storage.implementation.sql.connection;

import team.floracore.common.plugin.*;

import java.sql.*;
import java.util.function.*;

public interface ConnectionFactory {

    String getImplementationName();

    void init(FloraCorePlugin plugin);

    void shutdown() throws Exception;

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;

}
