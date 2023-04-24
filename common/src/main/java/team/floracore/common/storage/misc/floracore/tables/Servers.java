package team.floracore.common.storage.misc.floracore.tables;

import team.floracore.api.server.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.*;

import java.sql.*;

public class Servers extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}servers' WHERE name=?";
    public static final String DELETE = "DELETE FROM '{prefix}servers' WHERE name=?";
    private static final String UPDATE_NAME = "UPDATE '{prefix}servers' SET name=? WHERE name=?";
    private static final String UPDATE_TYPE = "UPDATE '{prefix}servers' SET type=? WHERE name=?";
    private static final String UPDATE_AUTO_SYNC = "UPDATE '{prefix}servers' SET autoSync=? WHERE name=?";
    private static final String UPDATE_LAST_ACTIVE_TIME = "UPDATE '{prefix}servers' SET lastActiveTime=? WHERE name=?";
    private static final String INSERT = "INSERT INTO '{prefix}servers' (name, type, autoSync, lastActiveTime) VALUES(?, ?, ?, ?)";

    private final int id;

    private String name;

    private ServerType type;

    private boolean autoSync;
    private long lastActiveTime;

    public Servers(FloraCorePlugin plugin, StorageImplementation storageImplementation, int id, String name, ServerType type, boolean autoSync, long lastActiveTime) {
        super(plugin, storageImplementation);
        this.id = id;
        this.name = name;
        this.type = type;
        this.autoSync = autoSync;
        this.lastActiveTime = lastActiveTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_NAME))) {
                ps.setString(1, name);
                ps.setString(2, name);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_TYPE))) {
                ps.setString(1, type.getName());
                ps.setString(2, name);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_AUTO_SYNC))) {
                ps.setBoolean(1, autoSync);
                ps.setString(2, name);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_LAST_ACTIVE_TIME))) {
                ps.setLong(1, lastActiveTime);
                ps.setString(2, name);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws SQLException {
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(INSERT))) {
                ps.setString(1, name);
                ps.setString(2, type.getName());
                ps.setBoolean(3, autoSync);
                ps.setLong(4, lastActiveTime);
                ps.execute();
            }
        }
    }
}
