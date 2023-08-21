package team.floracore.bukkit.util.wrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target(METHOD)
public @interface WrappedBukkitConstructor {
    float minVer() default Float.MIN_VALUE;

    float maxVer() default Float.MAX_VALUE;
}
