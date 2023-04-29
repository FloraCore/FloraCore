package team.floracore.common.storage.misc.floracore.tables;

import com.google.gson.*;
import org.floracore.api.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.*;

import java.sql.*;
import java.util.*;

public class Chat extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}chat' WHERE name=?";
    public static final String SELECT_WITH_START_TIME = "SELECT * FROM '{prefix}chat' WHERE name=? AND startTime=?";
    public static final String DELETE = "DELETE FROM '{prefix}chat' WHERE name=?";
    private static final String UPDATE_RECORDS = "UPDATE '{prefix}chat' SET records=? WHERE id=?";
    private static final String UPDATE_END_TIME = "UPDATE '{prefix}chat' SET endTime=? WHERE id=?";
    private static final String INSERT = "INSERT INTO '{prefix}chat' (name, records, startTime) VALUES(?, ?, ?)";

    private final int id;
    private final String name;
    private final long startTime;
    private List<ChatRecord> records;
    private long endTime;

    public Chat(FloraCorePlugin plugin, StorageImplementation storageImplementation, String name, long startTime) {
        super(plugin, storageImplementation);
        this.id = -1;
        this.name = name;
        this.records = new ArrayList<>();
        this.startTime = startTime;
    }

    public Chat(FloraCorePlugin plugin, StorageImplementation storageImplementation, int id, String name, List<ChatRecord> records, long startTime, long endTime) {
        super(plugin, storageImplementation);
        this.id = id;
        this.name = name;
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

    public List<ChatRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ChatRecord> records) {
        this.records = records;
        Gson gson = new Gson();
        String recordsJson = gson.toJson(records);
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
        Gson gson = new Gson();
        String recordsJson = gson.toJson(records);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(INSERT))) {
                ps.setString(1, name);
                ps.setString(2, recordsJson);
                ps.setLong(3, startTime);
                ps.execute();
            }
        }
    }
}
