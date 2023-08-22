package team.floracore.common.storage;

import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

/**
 * 数据库链接类型。
 */
@Getter
public enum StorageType {
	// Remote databases
	MARIADB("MariaDB", "mariadb"),
	MYSQL("MySQL", "mysql"),
	POSTGRESQL("PostgreSQL", "postgresql"),

	// Local databases
	SQLITE("SQLite", "sqlite"),
	H2("H2", "h2");

	private final String name;

	private final List<String> identifiers;

	StorageType(String name, String... identifiers) {
		this.name = name;
		this.identifiers = ImmutableList.copyOf(identifiers);
	}

	public static StorageType parse(String name, StorageType def) {
		for (StorageType t : values()) {
			for (String id : t.getIdentifiers()) {
				if (id.equalsIgnoreCase(name)) {
					return t;
				}
			}
		}
		return def;
	}

}
