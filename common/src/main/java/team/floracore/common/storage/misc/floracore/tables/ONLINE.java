package team.floracore.common.storage.misc.floracore.tables;

import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.AbstractFloraCoreTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ONLINE extends AbstractFloraCoreTable {
	public static final String SELECT = "SELECT * FROM '{prefix}online' WHERE uuid=?";
	public static final String DELETE = "DELETE FROM '{prefix}online' WHERE uuid=?";
	private static final String UPDATE_STATUS_TRUE = "UPDATE '{prefix}online' SET status=? WHERE uuid=?";
	private static final String UPDATE_STATUS_FALSE = "UPDATE '{prefix}online' SET status=? WHERE uuid=? AND " +
			"serverName=?";
	private static final String UPDATE_SERVER_NAME = "UPDATE '{prefix}online' SET serverName=? WHERE uuid=?";
	private static final String INSERT = "INSERT INTO '{prefix}online' (uuid, status, serverName) VALUES(?, ?, ?)";

	private final UUID uuid;
	private boolean status;
	private String serverName;

	public ONLINE(FloraCorePlugin plugin,
	              StorageImplementation storageImplementation,
	              UUID uuid,
	              boolean status,
	              String serverName) {
		super(plugin, storageImplementation);
		this.uuid = uuid;
		this.status = status;
		this.serverName = serverName;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
			                                                                                  .apply(UPDATE_SERVER_NAME))) {
				ps.setString(1, serverName);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatusTrue() {
		this.status = true;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
			                                                                                  .apply(UPDATE_STATUS_TRUE))) {
				ps.setBoolean(1, true);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setStatusFalse(String serverName) {
		this.status = true;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
			                                                                                  .apply(UPDATE_STATUS_FALSE))) {
				ps.setBoolean(1, false);
				ps.setString(2, uuid.toString());
				ps.setString(3, serverName);
				ps.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init() throws SQLException {
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
			                                                                                  .apply(INSERT))) {
				ps.setString(1, uuid.toString());
				ps.setBoolean(2, status);
				ps.setString(3, serverName);
				ps.execute();
			}
		}
	}
}
