package team.floracore.common.storage.misc.floracore.tables;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.*;
import team.floracore.common.util.gson.*;

import java.sql.*;
import java.util.*;

public class PARTY extends AbstractFloraCoreTable {
    public static final String SELECT_UUID = "SELECT * FROM '{prefix}party' WHERE uuid=?";
    public static final String DELETE = "DELETE FROM '{prefix}party' WHERE uuid=?";
    private static final String UPDATE_LEADER = "UPDATE '{prefix}party' SET leader=? WHERE uuid=?";
    private static final String UPDATE_MODERATORS = "UPDATE '{prefix}party' SET moderators=? WHERE uuid=?";
    private static final String UPDATE_MEMBERS = "UPDATE '{prefix}party' SET members=? WHERE uuid=?";
    private static final String UPDATE_SETTINGS = "UPDATE '{prefix}party' SET settings=? WHERE uuid=?";
    private static final String UPDATE_DISBAND_TIME = "UPDATE '{prefix}party' SET disbandTime=? WHERE uuid=?";
    private static final String INSERT = "INSERT INTO '{prefix}party' (uuid, leader, moderators, members, settings, createTime, disbandTime, chat) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    private final int id;
    private final UUID uuid;
    private final long createTime;
    private final int chat;
    private UUID leader;
    private List<UUID> moderators;
    private List<UUID> members;
    private String settings;
    private long disbandTime;

    public PARTY(FloraCorePlugin plugin, StorageImplementation storageImplementation, int id, UUID uuid, UUID leader, List<UUID> moderators, List<UUID> members, String settings, long createTime, long disbandTime, int chat) {
        super(plugin, storageImplementation);
        this.id = id;
        this.uuid = uuid;
        this.leader = leader;
        this.moderators = moderators;
        this.members = members;
        this.settings = settings;
        this.createTime = createTime;
        this.disbandTime = disbandTime;
        this.chat = chat;
    }

    public int getId() {
        return id;
    }

    public int getChat() {
        return chat;
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
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_DISBAND_TIME))) {
                ps.setLong(1, disbandTime);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_SETTINGS))) {
                ps.setString(1, settings);
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
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_LEADER))) {
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
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_MEMBERS))) {
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
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(UPDATE_MODERATORS))) {
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
        try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor().apply(INSERT))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, leader.toString());
                ps.setString(3, moderatorsJson);
                ps.setString(4, membersJson);
                ps.setString(5, settings);
                ps.setLong(6, createTime);
                ps.setLong(7, disbandTime);
                ps.setInt(8, chat);
                ps.execute();
            }
        }
    }
}
