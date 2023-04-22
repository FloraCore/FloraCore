package team.floracore.common.storage.misc.floracore.tables;

import team.floracore.api.data.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.sql.*;
import team.floracore.common.storage.misc.floracore.*;

import java.sql.*;
import java.util.*;

public class Data extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}data' WHERE uuid=?";
    public static final String DELETE_ALL = "DELETE FROM '{prefix}data' WHERE uuid=?";
    public static final String DELETE_TYPE = "DELETE FROM '{prefix}data' WHERE uuid=? AND type=?";
    public static final String DELETE_ID = "DELETE FROM '{prefix}data' WHERE id=?";
    private static final String UPDATE_VALUE = "UPDATE '{prefix}data' SET value=? WHERE uuid=? AND type=? AND data_key=?";
    private static final String UPDATE_EXPIRY = "UPDATE '{prefix}data' SET expiry=? WHERE uuid=? AND type=? AND data_key=?";
    private static final String INSERT = "INSERT INTO '{prefix}data' (uuid, type, data_key, value, expiry) VALUES(?, ?, ?, ?, ?)";
    private final int id;
    private final UUID uuid;
    private final DataType type;
    private final String key;
    private String value;
    private long expiry;

    public Data(FloraCorePlugin plugin, SqlStorage sqlStorage, int id, UUID uuid, DataType type, String key, String value, long expiry) {
        super(plugin, sqlStorage);
        this.id = id;
        this.uuid = uuid;
        this.type = type;
        this.key = key;
        this.value = value;
        this.expiry = expiry;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DataType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_VALUE))) {
                ps.setString(1, value);
                ps.setString(2, uuid.toString());
                ps.setString(3, type.getName());
                ps.setString(4, key);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_EXPIRY))) {
                ps.setLong(1, expiry);
                ps.setString(2, uuid.toString());
                ps.setString(3, type.getName());
                ps.setString(4, key);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws SQLException {
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(INSERT))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, type.getName());
                ps.setString(3, key);
                ps.setString(4, value);
                ps.setLong(5, expiry);
                ps.execute();
            }
        }
    }
}
