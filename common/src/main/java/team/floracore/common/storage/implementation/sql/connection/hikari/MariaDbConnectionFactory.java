package team.floracore.common.storage.implementation.sql.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import team.floracore.common.storage.misc.StorageCredentials;

import java.util.function.Function;

public class MariaDbConnectionFactory extends HikariConnectionFactory {
    public MariaDbConnectionFactory(StorageCredentials configuration) {
        super(configuration);
    }

    @Override
    public String getImplementationName() {
        return "MariaDB";
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected void configureDatabase(HikariConfig config,
                                     String address,
                                     String port,
                                     String databaseName,
                                     String username,
                                     String password) {
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl("jdbc:mariadb://" + address + ":" + port + "/" + databaseName);
        config.setUsername(username);
        config.setPassword(password);
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();

        // Calling Class.forName("org.mariadb.jdbc.Driver") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been setup.
        deregisterDriver("org.mariadb.jdbc.Driver");
    }
}
