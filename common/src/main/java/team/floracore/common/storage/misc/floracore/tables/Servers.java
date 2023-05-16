package team.floracore.common.storage.misc.floracore.tables;

import org.floracore.api.server.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.*;

import java.sql.*;

public class Servers extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}server' WHERE name=?";
    public static final String DELETE = "DELETE FROM '{prefix}server' WHERE name=?";
    private static final String UPDATE_NAME = "UPDATE '{prefix}server' SET name=? WHERE name=?";
    private static final String UPDATE_TYPE = "UPDATE '{prefix}server' SET type=? WHERE name=?";
    private static final String UPDATE_AUTO_SYNC_1 = "UPDATE '{prefix}server' SET autoSync1=? WHERE name=?";
    private static final String UPDATE_AUTO_SYNC_2 = "UPDATE '{prefix}server' SET autoSync2=? WHERE name=?";
    private static final String UPDATE_LAST_ACTIVE_TIME = "UPDATE '{prefix}server' SET lastActiveTime=? WHERE name=?";
    private static final String INSERT = "INSERT INTO '{prefix}server' (name, type, autoSync1, autoSync2, lastActiveTime) VALUES(?, ?, ?, ?, ?)";

    private final int id;
    private String name;
    private ServerType type;
    private boolean autoSync1;
    private boolean autoSync2;
    private long lastActiveTime;

    public Servers(FloraCorePlugin plugin, StorageImplementation storageImplementation, int id, String name, ServerType type, boolean autoSync1, boolean autoSync2, long lastActiveTime) {
        super(plugin, storageImplementation);
        this.id = id;
        this.name = name;
        this.type = type;
        this.autoSync1 = autoSync1;
        this.autoSync2 = autoSync2;
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

    public boolean isAutoSync1() {
        return autoSync1;
    }

    public void setAutoSync1(boolean autoSync1) {
        this.autoSync1 = autoSync1;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_AUTO_SYNC_1))) {
                ps.setBoolean(1, autoSync1);
                ps.setString(2, name);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAutoSync2() {
        return autoSync2;
    }

    public void setAutoSync2(boolean autoSync2) {
        this.autoSync2 = autoSync2;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_AUTO_SYNC_2))) {
                ps.setBoolean(1, autoSync2);
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
                ps.setBoolean(3, autoSync1);
                ps.setBoolean(4, autoSync2);
                ps.setLong(5, lastActiveTime);
                ps.execute();
            }
        }
    }
}
