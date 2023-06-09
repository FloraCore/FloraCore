package team.floracore.common.storage.misc.floracore.tables;

import org.floracore.api.commands.report.ReportStatus;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.AbstractFloraCoreTable;
import team.floracore.common.util.gson.GsonProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class REPORT extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}report' LIMIT 300";
    public static final String SELECT_REPORTED_UUID = "SELECT * FROM '{prefix}report' WHERE reported=? LIMIT 100";
    public static final String SELECT_UUID = "SELECT * FROM '{prefix}report' WHERE uuid=? LIMIT 100";
    public static final String DELETE = "DELETE FROM '{prefix}report' WHERE uuid=?";
    private static final String UPDATE_REPORTERS = "UPDATE '{prefix}report' SET reporters=? WHERE uuid=?";
    private static final String UPDATE_REASONS = "UPDATE '{prefix}report' SET reasons=? WHERE uuid=?";
    private static final String UPDATE_STATUS = "UPDATE '{prefix}report' SET status=? WHERE uuid=?";
    private static final String UPDATE_CONCLUSION_TIME = "UPDATE '{prefix}report' SET conclusionTime=? WHERE uuid=?";
    private static final String INSERT = "INSERT INTO '{prefix}report' (uuid, reporters, reported, reasons, " +
            "reportTime, status, conclusionTime) VALUES(?, ?, ?, ?, ?, ?, ?)";

    private final int id;
    private final UUID uuid;
    private final UUID reported;
    private final long reportTime;
    private List<String> reasons;
    private List<UUID> reporters;
    private ReportStatus status;
    private Long conclusionTime;

    public REPORT(FloraCorePlugin plugin,
                  StorageImplementation storageImplementation,
                  int id,
                  UUID uuid,
                  List<UUID> reporters,
                  UUID reported,
                  List<String> reasons,
                  long reportTime,
                  ReportStatus status,
                  Long conclusionTime) {
        super(plugin, storageImplementation);
        this.id = id;
        this.uuid = uuid;
        this.reporters = reporters;
        this.reported = reported;
        this.reasons = reasons;
        this.reportTime = reportTime;
        this.status = status;
        this.conclusionTime = conclusionTime;
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Long getConclusionTime() {
        return conclusionTime;
    }

    public void setConclusionTime(Long conclusionTime) {
        this.conclusionTime = conclusionTime;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_CONCLUSION_TIME))) {
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

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_STATUS))) {
                ps.setString(1, status.name());
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UUID> getReporters() {
        return reporters;
    }

    public void setReporters(List<UUID> reporters) {
        this.reporters = reporters;
        String reportersJson = GsonProvider.normal().toJson(reporters);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_REPORTERS))) {
                ps.setString(1, reportersJson);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getReported() {
        return reported;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
        String reasonsJson = GsonProvider.normal().toJson(reasons);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_REASONS))) {
                ps.setString(1, reasonsJson);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws SQLException {
        String reportersJson = GsonProvider.normal().toJson(reporters);
        String reasonsJson = GsonProvider.normal().toJson(reasons);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(INSERT))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, reportersJson);
                ps.setString(3, reported.toString());
                ps.setString(4, reasonsJson);
                ps.setLong(5, reportTime);
                ps.setString(6, status.name());
                ps.setLong(7, conclusionTime == null ? -1 : conclusionTime);
                ps.execute();
            }
        }
    }
}
