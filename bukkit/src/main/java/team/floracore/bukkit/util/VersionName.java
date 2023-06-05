package team.floracore.bukkit.util;

public @interface VersionName {
    float minVer() default Float.MIN_VALUE;

    float maxVer() default Float.MAX_VALUE;

    String[] value();
}
