package team.floracore.common.messaging.sql;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.*;
import org.floracore.api.messenger.message.*;

import java.sql.*;
import java.util.concurrent.locks.*;

/**
 * An implementation of {@link Messenger} using SQL.
 */
@SuppressWarnings("SqlResolve")
public abstract class AbstractSqlMessenger implements Messenger {
    private final IncomingMessageConsumer consumer;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private long lastId = -1;
    private boolean closed = false;

    protected AbstractSqlMessenger(IncomingMessageConsumer consumer) {
        this.consumer = consumer;
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract String getTableName();

    public void init() throws SQLException {
        try (Connection c = getConnection()) {
            // init table
            String createStatement = "CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (`id` INT AUTO_INCREMENT NOT NULL, `time` TIMESTAMP NOT NULL, `msg` TEXT NOT NULL, PRIMARY KEY (`id`)) DEFAULT CHARSET = utf8mb4";
            try (Statement s = c.createStatement()) {
                try {
                    s.execute(createStatement);
                } catch (SQLException e) {
                    if (e.getMessage().contains("Unknown character set")) {
                        // try again
                        s.execute(createStatement.replace("utf8mb4", "utf8"));
                    } else {
                        throw e;
                    }
                }
            }

            // pull last id
            try (PreparedStatement ps = c.prepareStatement("SELECT MAX(`id`) as `latest` FROM `" + getTableName() + "`")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        this.lastId = rs.getLong("latest");
                    }
                }
            }
        }
    }

    @Override
    public void sendOutgoingMessage(@NonNull OutgoingMessage outgoingMessage) {
        this.lock.readLock().lock();
        if (this.closed) {
            this.lock.readLock().unlock();
            return;
        }

        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO `" + getTableName() + "` (`time`, `msg`) VALUES(NOW(), ?)")) {
                ps.setString(1, outgoingMessage.asEncodedString());
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void pollMessages() {
        this.lock.readLock().lock();
        if (this.closed) {
            this.lock.readLock().unlock();
            return;
        }

        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT `id`, `msg` FROM `" + getTableName() + "` WHERE `id` > ? AND (NOW() - `time` < 30)")) {
                ps.setLong(1, this.lastId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        this.lastId = Math.max(this.lastId, id);
                        String message = rs.getString("msg");
                        this.consumer.consumeIncomingMessageAsString(message);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void runHousekeeping() {
        this.lock.readLock().lock();
        if (this.closed) {
            this.lock.readLock().unlock();
            return;
        }

        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM `" + getTableName() + "` WHERE (NOW() - `time` > 60)")) {
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        this.lock.writeLock().lock();
        try {
            this.closed = true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
