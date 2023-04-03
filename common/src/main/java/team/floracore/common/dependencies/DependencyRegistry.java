package team.floracore.common.dependencies;

import com.google.gson.*;

/**
 * Applies FloraCore specific behaviour for {@link Dependency}s.
 */
public class DependencyRegistry {

    @SuppressWarnings("ConstantConditions")
    public static boolean isGsonRelocated() {
        return JsonElement.class.getName().startsWith("team.floracore");
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean slf4jPresent() {
        return classExists("org.slf4j.Logger") && classExists("org.slf4j.LoggerFactory");
    }

    public boolean shouldAutoLoad(Dependency dependency) {
        switch (dependency) {
            // all used within 'isolated' classloaders, and are therefore not
            // relocated.
            case ASM:
            case ASM_COMMONS:
            case JAR_RELOCATOR:
            case SQLITE_DRIVER:
                return false;
            default:
                return true;
        }
    }

}
