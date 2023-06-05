package team.floracore.common.storage.implementation.sql.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import team.floracore.common.storage.misc.StorageCredentials;

import java.util.Map;
import java.util.function.Function;

public class PostgreConnectionFactory extends HikariConnectionFactory {
    public PostgreConnectionFactory(StorageCredentials configuration) {
        super(configuration);
    }

    @Override
    public String getImplementationName() {
        return "PostgreSQL";
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace('\'', '"');
    }

    @Override
    protected String defaultPort() {
        return "5432";
    }

    @Override
    protected void configureDatabase(HikariConfig config,
                                     String address,
                                     String port,
                                     String databaseName,
                                     String username,
                                     String password) {
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
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
    }
}
