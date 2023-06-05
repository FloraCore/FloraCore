package team.floracore.common.storage.implementation.sql.connection.file;

import team.floracore.common.storage.implementation.sql.connection.ConnectionFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Abstract {@link ConnectionFactory} using a file based database driver.
 */
abstract class FlatfileConnectionFactory implements ConnectionFactory {
    /**
     * Format used for formatting database file size.
     */
    protected static final DecimalFormat FILE_SIZE_FORMAT = new DecimalFormat("#.##");
    /**
     * The path to the database file
     */
    private final Path file;
    /**
     * The current open connection, if any
     */
    private NonClosableConnection connection;

    FlatfileConnectionFactory(Path file) {
        this.file = file;
    }

    @Override
    public void shutdown() throws Exception {
        if (this.connection != null) {
            this.connection.shutdown();
        }
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        NonClosableConnection connection = this.connection;
        if (connection == null || connection.isClosed()) {
            connection = new NonClosableConnection(createConnection(this.file));
            this.connection = connection;
        }
        return connection;
    }

    /**
     * Creates a connection to the database.
     *
     * @param file the database file
     * @return the connection
     * @throws java.sql.SQLException if any error occurs
     */
    protected abstract Connection createConnection(Path file) throws SQLException;

    protected void migrateOldDatabaseFile(String oldName) {
        Path oldFile = getWriteFile().getParent().resolve(oldName);
        if (Files.exists(oldFile)) {
            try {
                Files.move(oldFile, getWriteFile());
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Gets the path of the file the database driver actually ends up writing to.
     *
     * @return the write file
     */
    protected Path getWriteFile() {
        return this.file;
    }
}
