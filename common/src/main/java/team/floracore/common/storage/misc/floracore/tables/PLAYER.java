package team.floracore.common.storage.misc.floracore.tables;

import lombok.Getter;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.AbstractFloraCoreTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PLAYER extends AbstractFloraCoreTable {
	public static final String SELECT = "SELECT * FROM '{prefix}player' WHERE uuid=? ORDER BY id DESC";
	public static final String SELECT_NAME = "SELECT * FROM '{prefix}player' WHERE name=? ORDER BY id DESC";
	public static final String DELETE = "DELETE FROM '{prefix}player' WHERE uuid=?";
	private static final String UPDATE_NAME = "UPDATE '{prefix}player' SET name=? WHERE uuid=?";
	private static final String UPDATE_LAST_LOGIN_IP = "UPDATE '{prefix}player' SET lastLoginIp=? WHERE uuid=?";
	private static final String UPDATE_LAST_LOGIN_TIME = "UPDATE '{prefix}player' SET lastLoginTime=? WHERE uuid=?";
	private static final String UPDATE_PLAY_TIME = "UPDATE '{prefix}player' SET playTime=? WHERE uuid=?";
	private static final String INSERT = "INSERT INTO '{prefix}player' (uuid, name, firstLoginIp, lastLoginIp, " +
			"firstLoginTime, lastLoginTime, playTime) VALUES(?, ?, ?, ?, ?, ?, ?)";

	@Getter
	private final int id;
	private final UUID uuid;
	@Getter
	private final long firstLoginTime;
	@Getter
	private final String firstLoginIp;
	@Getter
	private String name;
	@Getter
	private String lastLoginIp;
	@Getter
	private long lastLoginTime;
	@Getter
	private long playTime;

	public PLAYER(FloraCorePlugin plugin,
	              StorageImplementation storageImplementation,
	              int id,
	              UUID uuid,
	              String name,
	              String loginIp) {
		super(plugin, storageImplementation);
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.firstLoginIp = loginIp;
		this.lastLoginIp = loginIp;
		long currentTime = System.currentTimeMillis();
		this.firstLoginTime = currentTime;
		this.lastLoginTime = currentTime;
		this.playTime = 0;
	}

	public PLAYER(FloraCorePlugin plugin,
	              StorageImplementation storageImplementation,
	              int id,
	              UUID uuid,
	              String name,
	              String firstLoginIp,
	              String lastLoginIp,
	              long firstLoginTime,
	              long lastLoginTime,
	              long playTime) {
		super(plugin, storageImplementation);
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.firstLoginIp = firstLoginIp;
		this.lastLoginIp = lastLoginIp;
		this.firstLoginTime = firstLoginTime;
		this.lastLoginTime = lastLoginTime;
		this.playTime = playTime;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public void setName(String name) {
		this.name = name;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(UPDATE_NAME))) {
				ps.setString(1, name);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(UPDATE_LAST_LOGIN_IP))) {
				ps.setString(1, lastLoginIp);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(UPDATE_LAST_LOGIN_TIME))) {
				ps.setLong(1, lastLoginTime);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setPlayTime(long playTime) throws SQLException {
		this.playTime = playTime;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(UPDATE_PLAY_TIME))) {
				ps.setLong(1, playTime);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
		}
	}

	@Override
	public void init() throws SQLException {
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(INSERT))) {
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
