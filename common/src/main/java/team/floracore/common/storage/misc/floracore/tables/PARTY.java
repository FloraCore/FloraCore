package team.floracore.common.storage.misc.floracore.tables;

import org.floracore.api.socialsystems.party.PartySettings;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.AbstractFloraCoreTable;
import team.floracore.common.util.gson.GsonProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PARTY extends AbstractFloraCoreTable {
    public static final String SELECT_UUID = "SELECT * FROM '{prefix}party' WHERE uuid=?";
    public static final String DELETE = "DELETE FROM '{prefix}party' WHERE uuid=?";
    private static final String UPDATE_LEADER = "UPDATE '{prefix}party' SET leader=? WHERE uuid=?";
    private static final String UPDATE_MODERATORS = "UPDATE '{prefix}party' SET moderators=? WHERE uuid=?";
    private static final String UPDATE_MEMBERS = "UPDATE '{prefix}party' SET members=? WHERE uuid=?";
    private static final String UPDATE_SETTINGS = "UPDATE '{prefix}party' SET settings=? WHERE uuid=?";
    private static final String UPDATE_DISBAND_TIME = "UPDATE '{prefix}party' SET disbandTime=? WHERE uuid=?";
    private static final String INSERT = "INSERT INTO '{prefix}party' (uuid, leader, moderators, members, settings, " +
            "createTime, disbandTime) VALUES(?, ?, ?, ?, ?, ?, ?)";

    private final int id;
    private final UUID uuid;
    private final long createTime;
    private UUID leader;
    private List<UUID> moderators;
    /**
     * 包括leader、moderators和members
     */
    private List<UUID> members;
    private PartySettings settings;
    private long disbandTime;

    public PARTY(FloraCorePlugin plugin,
                 StorageImplementation storageImplementation,
                 int id,
                 UUID uuid,
                 UUID leader,
                 List<UUID> moderators,
                 List<UUID> members,
                 PartySettings settings,
                 long createTime,
                 long disbandTime) {
        super(plugin, storageImplementation);
        this.id = id;
        this.uuid = uuid;
        this.leader = leader;
        this.moderators = moderators;
        this.members = members;
        this.settings = settings;
        this.createTime = createTime;
        this.disbandTime = disbandTime;
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getDisbandTime() {
        return disbandTime;
    }

    public void setDisbandTime(long disbandTime) {
        this.disbandTime = disbandTime;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_DISBAND_TIME))) {
                ps.setLong(1, disbandTime);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PartySettings getSettings() {
        return settings;
    }

    public void setSettings(PartySettings settings) {
        this.settings = settings;
        String settingsJson = GsonProvider.normal().toJson(settings);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_SETTINGS))) {
                ps.setString(1, settingsJson);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_LEADER))) {
                ps.setString(1, leader.toString());
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
        String membersJson = GsonProvider.normal().toJson(members);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_MEMBERS))) {
                ps.setString(1, membersJson);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UUID> getModerators() {
        return moderators;
    }

    public void setModerators(List<UUID> moderators) {
        this.moderators = moderators;
        String moderatorsJson = GsonProvider.normal().toJson(moderators);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(UPDATE_MODERATORS))) {
                ps.setString(1, moderatorsJson);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws SQLException {
        String moderatorsJson = GsonProvider.normal().toJson(moderators);
        String membersJson = GsonProvider.normal().toJson(members);
        String settingsJson = GsonProvider.normal().toJson(settings);
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
                    .apply(INSERT))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, leader.toString());
                ps.setString(3, moderatorsJson);
                ps.setString(4, membersJson);
                ps.setString(5, settingsJson);
                ps.setLong(6, createTime);
                ps.setLong(7, disbandTime);
                ps.execute();
            }
        }
    }
}
