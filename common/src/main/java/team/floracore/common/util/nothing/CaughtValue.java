package team.floracore.common.util.nothing;

import java.lang.annotation.*;

/**
 * The type of formal parameter can only be Throwable
 *
 * @see Throwable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.PARAMETER)
public @interface CaughtValue {
}
