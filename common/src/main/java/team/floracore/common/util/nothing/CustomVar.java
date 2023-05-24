package team.floracore.common.util.nothing;

import java.lang.annotation.*;

/**
 * Visit custom variable with a name
 * It will be created when it doesn't exist
 * To interact between injectors
 * Be careful not to use overly simplistic name
 *
 * @see LocalVar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.PARAMETER)
public @interface CustomVar {
    String value();
}
