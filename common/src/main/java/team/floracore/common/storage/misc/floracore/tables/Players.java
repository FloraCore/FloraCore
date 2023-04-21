package team.floracore.common.storage.misc.floracore.tables;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.sql.*;
import team.floracore.common.storage.misc.floracore.*;

import java.sql.*;
import java.util.Date;
import java.util.*;

public class Players extends AbstractFloraCoreTable {
    public static final String SELECT = "SELECT * FROM '{prefix}players' WHERE uuid=?";
    public static final String DELETE = "DELETE FROM '{prefix}players' WHERE uuid=?";
    private static final String UPDATE_NAME = "UPDATE '{prefix}players' SET name=? WHERE uuid=?";
    private static final String UPDATE_LAST_LOGIN_IP = "UPDATE '{prefix}players' SET lastLoginIp=? WHERE uuid=?";
    private static final String UPDATE_LAST_LOGIN_TIME = "UPDATE '{prefix}players' SET lastLoginTime=? WHERE uuid=?";
    private static final String UPDATE_PLAY_TIME = "UPDATE '{prefix}players' SET playTime=? WHERE uuid=?";
    private static final String INSERT = "INSERT INTO '{prefix}players' (uuid, name, firstLoginIp, lastLoginIp, playTime) VALUES(?, ?, ?, ?, ?)";

    private final UUID uuid;
    private final Date firstLoginTime;
    private final String firstLoginIp;
    private String name;
    private String lastLoginIp;
    private Date lastLoginTime;
    private long playTime;

    public Players(FloraCorePlugin plugin, SqlStorage sqlStorage, UUID uuid, String name, String loginIp) {
        super(plugin, sqlStorage);
        this.uuid = uuid;
        this.name = name;
        this.firstLoginIp = loginIp;
        this.lastLoginIp = loginIp;
        Date date = new Date();
        this.firstLoginTime = date;
        this.lastLoginTime = date;
        this.playTime = 0;
    }

    public Players(FloraCorePlugin plugin, SqlStorage sqlStorage, UUID uuid, String name, String firstLoginIp, String lastLoginIp, Date firstLoginTime, Date lastLoginTime, long playTime) {
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

    public void setName(String name) throws SQLException {
        this.name = name;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_NAME))) {
                ps.setString(1, name);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    public String getFirstLoginIp() {
        return firstLoginIp;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) throws SQLException {
        this.lastLoginIp = lastLoginIp;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_LAST_LOGIN_IP))) {
                ps.setString(1, lastLoginIp);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    public Date getFirstLoginTime() {
        return firstLoginTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) throws SQLException {
        this.lastLoginTime = lastLoginTime;
        try (Connection connection = getSqlStorage().getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(getSqlStorage().getStatementProcessor().apply(UPDATE_LAST_LOGIN_TIME))) {
                ps.setDate(1, (java.sql.Date) lastLoginTime);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
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
                ps.setLong(5, playTime);
                ps.execute();
            }
        }
    }
}
