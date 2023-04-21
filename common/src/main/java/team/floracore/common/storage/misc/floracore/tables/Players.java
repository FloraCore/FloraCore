package team.floracore.common.storage.misc.floracore.tables;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.sql.*;
import team.floracore.common.storage.misc.floracore.*;

import java.sql.*;
import java.util.*;

public class Players extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}players' WHERE uuid=?";
    public static final String SELECT_NAME = "SELECT * FROM '{prefix}players' WHERE name=?";
    public static final String DELETE = "DELETE FROM '{prefix}players' WHERE uuid=?";
    private static final String UPDATE_NAME = "UPDATE '{prefix}players' SET name=? WHERE uuid=?";
    private static final String UPDATE_LAST_LOGIN_IP = "UPDATE '{prefix}players' SET lastLoginIp=? WHERE uuid=?";
    private static final String UPDATE_LAST_LOGIN_TIME = "UPDATE '{prefix}players' SET lastLoginTime=? WHERE uuid=?";
    private static final String UPDATE_PLAY_TIME = "UPDATE '{prefix}players' SET playTime=? WHERE uuid=?";
    private static final String INSERT = "INSERT INTO '{prefix}players' (uuid, name, firstLoginIp, lastLoginIp, firstLoginTime, lastLoginTime, playTime) VALUES(?, ?, ?, ?, ?, ?, ?)";

    private final UUID uuid;
    private final long firstLoginTime;
    private final String firstLoginIp;
    private String name;
    private String lastLoginIp;
    private long lastLoginTime;
    private long playTime;

    public Players(FloraCorePlugin plugin, SqlStorage sqlStorage, UUID uuid, String name, String loginIp) {
        super(plugin, sqlStorage);
        this.uuid = uuid;
        this.name = name;
        this.firstLoginIp = loginIp;
        this.lastLoginIp = loginIp;
        long currentTime = System.currentTimeMillis();
        this.firstLoginTime = currentTime;
        this.lastLoginTime = currentTime;
        this.playTime = 0;
    }

    public Players(FloraCorePlugin plugin, SqlStorage sqlStorage, UUID uuid, String name, String firstLoginIp, String lastLoginIp, long firstLoginTime, long lastLoginTime, long playTime) {
        super(plugin, sqlStorage);
        this.uuid = uuid;
        this.name = name;
        this.firstLoginIp = firstLoginIp;
        this.lastLoginIp = lastLoginIp;
        this.firstLoginTime = firstLoginTime;
        this.lastLoginTime = lastLoginTime;
        this.playTime = playTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_NAME))) {
                ps.setString(1, name);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFirstLoginIp() {
        return firstLoginIp;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_LAST_LOGIN_IP))) {
                ps.setString(1, lastLoginIp);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_LAST_LOGIN_TIME))) {
                ps.setLong(1, lastLoginTime);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) throws SQLException {
        this.playTime = playTime;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_PLAY_TIME))) {
                ps.setLong(1, playTime);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    @Override
    public void init() throws SQLException {
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(INSERT))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, name);
                ps.setString(3, firstLoginIp);
                ps.setString(4, lastLoginIp);
                ps.setLong(5, firstLoginTime);
                ps.setLong(6, lastLoginTime);
                ps.setLong(7, playTime);
                ps.execute();
            }
        }
    }
}
