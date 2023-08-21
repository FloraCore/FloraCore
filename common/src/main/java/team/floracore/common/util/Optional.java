package team.floracore.common.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Means the marked item is dispensable
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Optional {
}
