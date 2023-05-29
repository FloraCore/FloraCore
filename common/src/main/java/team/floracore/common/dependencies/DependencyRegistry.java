package team.floracore.common.dependencies;

import com.google.common.collect.*;
import com.google.gson.*;
import team.floracore.common.storage.*;

import java.util.*;

/**
 * Applies FloraCore specific behaviour for {@link Dependency}s.
 */
public class DependencyRegistry {
    // @formatter:off
    private static final SetMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableSetMultimap.<StorageType, Dependency>builder()
            .putAll(StorageType.MARIADB,        Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE, Dependency.HIKARI, Dependency.MARIADB_DRIVER)
            .putAll(StorageType.MYSQL,          Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE, Dependency.HIKARI, Dependency.MYSQL_DRIVER)
            .putAll(StorageType.POSTGRESQL,     Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE, Dependency.HIKARI, Dependency.POSTGRESQL_DRIVER)
            .putAll(StorageType.SQLITE,         Dependency.SQLITE_DRIVER)
            .putAll(StorageType.H2,             Dependency.H2_DRIVER)
            .build();


    @SuppressWarnings("ConstantConditions")
    public static boolean isGsonRelocated() {
        return JsonElement.class.getName().startsWith("team.floracore");
    }

    private static boolean slf4jPresent() {
        return classExists("org.slf4j.Logger") && classExists("org.slf4j.LoggerFactory");
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Set<Dependency> resolveStorageDependencies(Set<StorageType> storageTypes, boolean redis) {
        Set<Dependency> dependencies = new LinkedHashSet<>();
        for (StorageType storageType : storageTypes) {
            dependencies.addAll(STORAGE_DEPENDENCIES.get(storageType));
        }

        if (redis) {
            dependencies.add(Dependency.COMMONS_POOL_2);
            dependencies.add(Dependency.JEDIS);
            dependencies.add(Dependency.SLF4J_API);
            dependencies.add(Dependency.SLF4J_SIMPLE);
        }

        // don't load slf4j if it's already present
        if ((dependencies.contains(Dependency.SLF4J_API) || dependencies.contains(Dependency.SLF4J_SIMPLE)) && slf4jPresent()) {
            dependencies.remove(Dependency.SLF4J_API);
            dependencies.remove(Dependency.SLF4J_SIMPLE);
        }

        return dependencies;
    }

    public boolean shouldAutoLoad(Dependency dependency) {
        switch (dependency) {
            // all used within 'isolated' classloaders, and are therefore not
            // relocated.
            case ASM:
            case ASM_COMMONS:
            case JAR_RELOCATOR:
            case H2_DRIVER:
            case H2_DRIVER_LEGACY:
            case SQLITE_DRIVER:
                return false;
            default:
                return true;
        }
    }

}
