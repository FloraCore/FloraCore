package team.floracore.common.storage.misc.floracore.tables;

import org.floracore.api.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.*;
import team.floracore.common.util.gson.*;

import java.sql.*;
import java.util.*;

public class Report extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}report' WHERE uuid=?";
    public static final String DELETE = "DELETE FROM '{prefix}report' WHERE uuid=?";
    private static final String UPDATE_HANDLER = "UPDATE '{prefix}report' SET handler=? WHERE uuid=?";
    private static final String UPDATE_HANDLE_TIME = "UPDATE '{prefix}report' SET handleTime=? WHERE uuid=?";
    private static final String UPDATE_CONCLUSION = "UPDATE '{prefix}report' SET conclusion=? WHERE uuid=?";
    private static final String UPDATE_CONCLUSION_TIME = "UPDATE '{prefix}report' SET conclusionTime=? WHERE uuid=?";
    private static final String INSERT = "INSERT INTO '{prefix}report' (uuid, reporter, reported, reason, reportTime, handler, handleTime, conclusion, conclusionTime, chat) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final int id;
    private final UUID uuid;
    private final UUID reporter;
    private final UUID reported;
    private final String reason;
    private final long reportTime;
    private final List<DataChatRecord> chat;
    private UUID handler;
    private Long handleTime;
    private Boolean conclusion;
    private Long conclusionTime;

    public Report(FloraCorePlugin plugin, StorageImplementation storageImplementation, int id, UUID uuid, UUID reporter, UUID reported, String reason, long reportTime, UUID handler, Long handleTime, Boolean conclusion, Long conclusionTime, List<DataChatRecord> chat) {
        super(plugin, storageImplementation);
        this.id = id;
        this.uuid = uuid;
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.reportTime = reportTime;
        this.handler = handler;
        this.handleTime = handleTime;
        this.conclusion = conclusion;
        this.conclusionTime = conclusionTime;
        this.chat = chat;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getConclusionTime() {
        return conclusionTime;
    }

    public void setConclusionTime(Long conclusionTime) {
        this.conclusionTime = conclusionTime;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_CONCLUSION_TIME))) {
                ps.setLong(1, conclusionTime);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getReportTime() {
        return reportTime;
    }

    public Long getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Long handleTime) {
        this.handleTime = handleTime;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_HANDLE_TIME))) {
                ps.setLong(1, handleTime);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DataChatRecord> getChat() {
        return chat;
    }

    public Boolean getConclusion() {
        return conclusion;
    }

    public void setConclusion(Boolean conclusion) {
        this.conclusion = conclusion;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_CONCLUSION))) {
                ps.setBoolean(1, conclusion);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getHandler() {
        return handler;
    }

    public void setHandler(UUID handler) {
        this.handler = handler;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_HANDLER))) {
                ps.setString(1, handler.toString());
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getReporter() {
        return reporter;
    }

    public UUID getReported() {
        return reported;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void init() throws SQLException {
        String chatJson = GsonProvider.normal().toJson(chat);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(INSERT))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, reporter.toString());
                ps.setString(3, reported.toString());
                ps.setString(4, reason);
                ps.setLong(5, reportTime);
                ps.setString(6, handler.toString());
                ps.setLong(7, handleTime);
                ps.setBoolean(8, conclusion);
                ps.setLong(9, conclusionTime);
                ps.setString(9, chatJson);
                ps.execute();
            }
        }
    }
}
