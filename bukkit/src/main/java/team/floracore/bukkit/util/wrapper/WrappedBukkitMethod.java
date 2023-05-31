package team.floracore.bukkit.util.wrapper;

import team.floracore.bukkit.util.VersionName;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface WrappedBukkitMethod {
    VersionName[] value();
}
