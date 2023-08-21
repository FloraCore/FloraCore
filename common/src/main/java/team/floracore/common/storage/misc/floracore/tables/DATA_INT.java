package team.floracore.common.storage.misc.floracore.tables;

import org.floracore.api.data.DataType;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.AbstractFloraCoreTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DATA_INT extends AbstractFloraCoreTable {
	public static final String SELECT = "SELECT * FROM '{prefix}data_int' WHERE uuid=? LIMIT 100";
	public static final String SELECT_DESC = "SELECT * FROM '{prefix}data_int' WHERE type=? AND key=? ORDER BY value DESC LIMIT 300";
	public static final String SELECT_ASC = "SELECT * FROM '{prefix}data_int' WHERE type=? AND key=? ORDER BY value ASC LIMIT 300";
	public static final String DELETE_ALL = "DELETE FROM '{prefix}data_int' WHERE uuid=?";
	public static final String DELETE_TYPE = "DELETE FROM '{prefix}data_int' WHERE uuid=? AND type=?";
	public static final String DELETE_ID = "DELETE FROM '{prefix}data_int' WHERE id=?";
	private static final String UPDATE_VALUE = "UPDATE '{prefix}data_int' SET value=? WHERE uuid=? AND type=? AND " +
			"data_key=?";
	private static final String UPDATE_EXPIRY = "UPDATE '{prefix}data_int' SET expiry=? WHERE uuid=? AND type=? AND " +
			"data_key=?";
	private static final String INSERT = "INSERT INTO '{prefix}data_int' (uuid, type, data_key, value, expiry) VALUES(?, " +
			"?, ?, ?, ?)";
	private final int id;
	private final UUID uuid;
	private final DataType type;
	private final String key;
	private int value;
	private long expiry;

	public DATA_INT(FloraCorePlugin plugin,
	                StorageImplementation storageImplementation,
	                int id,
	                UUID uuid,
	                DataType type,
	                String key,
	                int value,
	                long expiry) {
		super(plugin, storageImplementation);
		this.id = id;
		this.uuid = uuid;
		this.type = type;
		this.key = key;
		this.value = value;
		this.expiry = expiry;
	}

	public int getId() {
		return id;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public DataType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(UPDATE_VALUE))) {
				ps.setInt(1, value);
				ps.setString(2, uuid.toString());
				ps.setString(3, type.getName());
				ps.setString(4, key);
				ps.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(UPDATE_EXPIRY))) {
				ps.setLong(1, expiry);
				ps.setString(2, uuid.toString());
				ps.setString(3, type.getName());
				ps.setString(4, key);
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
				ps.setString(2, type.getName());
				ps.setString(3, key);
				ps.setInt(4, value);
				ps.setLong(5, expiry);
				ps.execute();
			}
		}
	}
}
