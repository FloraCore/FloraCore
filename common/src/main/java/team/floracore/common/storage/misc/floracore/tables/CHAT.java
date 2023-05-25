package team.floracore.common.storage.misc.floracore.tables;

import org.floracore.api.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.*;
import team.floracore.common.util.gson.*;

import java.sql.*;
import java.util.*;

public class CHAT extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}chat' WHERE name=? AND type=?";
    public static final String SELECT_WITH_ID = "SELECT * FROM '{prefix}chat' WHERE id=?";
    public static final String SELECT_WITH_START_TIME = "SELECT * FROM '{prefix}chat' WHERE name=? AND startTime=? AND type=?";
    public static final String DELETE = "DELETE FROM '{prefix}chat' WHERE name=?";
    private static final String UPDATE_RECORDS = "UPDATE '{prefix}chat' SET records=? WHERE id=?";
    private static final String UPDATE_END_TIME = "UPDATE '{prefix}chat' SET endTime=? WHERE id=?";
    private static final String INSERT = "INSERT INTO '{prefix}chat' (name, type, records, startTime) VALUES(?, ?, ?, ?)";

    private final int id;
    private final String name;
    private final ChatType type;
    private final long startTime;
    private List<ChatRecord> records;
    private long endTime;

    public CHAT(FloraCorePlugin plugin,
                StorageImplementation storageImplementation,
                String name,
                ChatType type,
                long startTime) {
        super(plugin, storageImplementation);
        this.id = -1;
        this.name = name;
        this.type = type;
        this.records = new ArrayList<>();
        this.startTime = startTime;
    }

    public CHAT(FloraCorePlugin plugin,
                StorageImplementation storageImplementation,
                int id,
                String name,
                ChatType type,
                List<ChatRecord> records,
                long startTime,
                long endTime) {
        super(plugin, storageImplementation);
        this.id = id;
        this.name = name;
        this.type = type;
        this.records = records;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ChatType getType() {
        return type;
    }

    public List<ChatRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ChatRecord> records) {
        this.records = records;
        String recordsJson = GsonProvider.normal().toJson(records);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_RECORDS))) {
                ps.setString(1, recordsJson);
                ps.setInt(2, id);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_END_TIME))) {
                ps.setLong(1, endTime);
                ps.setInt(2, id);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws SQLException {
        String recordsJson = GsonProvider.normal().toJson(records);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(INSERT))) {
                ps.setString(1, name);
                ps.setString(2, type.name());
                ps.setString(3, recordsJson);
                ps.setLong(4, startTime);
                ps.execute();
            }
        }
    }
}
