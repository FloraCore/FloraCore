package team.floracore.common.storage.misc.floracore.tables;

import lombok.Getter;
import org.floracore.api.data.chat.ChatType;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.AbstractFloraCoreTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class CHAT extends AbstractFloraCoreTable {
	public static final String SELECT = "SELECT * FROM '{prefix}chat' WHERE uuid=? AND type=? ORDER BY time DESC LIMIT 500";
	public static final String SELECT_SERVER = "SELECT * FROM '{prefix}chat' WHERE type=? AND parameters=? ORDER BY time DESC LIMIT 1000";
	public static final String SELECT_TYPE = "SELECT * FROM '{prefix}chat' WHERE type=? ORDER BY time DESC LIMIT 1000";
	private static final String INSERT = "INSERT INTO '{prefix}chat' (type, parameters, uuid, message, time) VALUES(?, ?, ?, ?, ?)";

	@Getter
	private final int id;
	@Getter
	private final ChatType type;
	@Getter
	private final String parameters;
	private final UUID uuid;
	@Getter
	private final String message;
	@Getter
	private final long time;

	public CHAT(FloraCorePlugin plugin,
	            StorageImplementation storageImplementation,
	            int id, ChatType type, String parameters, UUID uuid, String message, long time) {
		super(plugin, storageImplementation);
		this.id = id;
		this.type = type;
		this.parameters = parameters;
		this.uuid = uuid;
		this.message = message;
		this.time = time;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public void init() throws SQLException {
		try (Connection connection = getStorageImplementation().getConnectionFactory().getConnection()) {
			try (PreparedStatement ps = connection.prepareStatement(getStorageImplementation().getStatementProcessor()
					.apply(INSERT))) {
				ps.setString(1, type.name());
				ps.setString(2, parameters);
				ps.setString(3, uuid.toString());
				ps.setString(4, message);
				ps.setLong(5, time);
				ps.execute();
			}
		}
	}
}
