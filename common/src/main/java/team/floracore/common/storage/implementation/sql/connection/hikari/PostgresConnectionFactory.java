package team.floracore.common.storage.implementation.sql.connection.hikari;

import com.zaxxer.hikari.*;
import team.floracore.common.storage.misc.*;

import java.util.*;
import java.util.function.*;

public class PostgresConnectionFactory extends HikariConnectionFactory {
    public PostgresConnectionFactory(StorageCredentials configuration) {
        super(configuration);
    }

    @Override
    public String getImplementationName() {
        return "PostgreSQL";
    }

    @Override
    protected String defaultPort() {
        return "5432";
    }

    @Override
    protected void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password) {
        config.setDataSourceClassName("com.impossibl.postgres.jdbc.PGDataSource");
        config.addDataSourceProperty("serverName", address);
        config.addDataSourceProperty("portNumber", Integer.parseInt(port));
        config.addDataSourceProperty("databaseName", databaseName);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
    }

    @Override
    protected void overrideProperties(Map<String, Object> properties) {
        super.overrideProperties(properties);

        // remove the default config properties which don't exist for PostgreSQL
        properties.remove("useUnicode");
        properties.remove("characterEncoding");

        // socketTimeout -> networkTimeout
        Object socketTimeout = properties.remove("socketTimeout");
        if (socketTimeout != null) {
            properties.putIfAbsent("networkTimeout", Integer.parseInt(socketTimeout.toString()));
        }
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace('\'', '"');
    }
}
