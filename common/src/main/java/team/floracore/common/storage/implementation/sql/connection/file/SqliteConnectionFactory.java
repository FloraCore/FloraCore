package team.floracore.common.storage.implementation.sql.connection.file;

import team.floracore.common.dependencies.Dependency;
import team.floracore.common.plugin.FloraCorePlugin;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Function;

public class SqliteConnectionFactory extends FlatfileConnectionFactory {
	private Constructor<?> connectionConstructor;

	public SqliteConnectionFactory(Path file) {
		super(file);
	}

	@Override
	public String getImplementationName() {
		return "SQLite";
	}

	@Override
	public void init(FloraCorePlugin plugin) {
		migrateOldDatabaseFile("floracore.sqlite");

		ClassLoader classLoader = plugin.getDependencyManager()
				.obtainClassLoaderWith(EnumSet.of(Dependency.SQLITE_DRIVER));
		try {
			Class<?> connectionClass = classLoader.loadClass("org.sqlite.jdbc4.JDBC4Connection");
			this.connectionConstructor = connectionClass.getConstructor(String.class, String.class, Properties.class);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Function<String, String> getStatementProcessor() {
		return s -> s.replace('\'', '`');
	}

	@Override
	protected Connection createConnection(Path file) throws SQLException {
		try {
			return (Connection) this.connectionConstructor.newInstance("jdbc:sqlite:" + file.toString(),
					file.toString(),
					new Properties());
		} catch (ReflectiveOperationException e) {
			if (e.getCause() instanceof SQLException) {
				throw (SQLException) e.getCause();
			}
			throw new RuntimeException(e);
		}
	}
}
