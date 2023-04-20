package team.floracore.common.storage;

import com.google.common.collect.*;

import java.util.*;

/**
 * 数据库链接类型。
 */
public enum StorageType {
    // Remote databases
    MARIADB("MariaDB", "mariadb"),
    MYSQL("MySQL", "mysql"),

    // Local databases
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

    public String getName() {
        return this.name;
    }

    public List<String> getIdentifiers() {
        return this.identifiers;
    }
}
