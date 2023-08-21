package team.floracore.bukkit.util.wrapper;

import team.floracore.bukkit.util.VersionName;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target(TYPE)
public @interface WrappedBukkitClass {
    VersionName[] value();
}
