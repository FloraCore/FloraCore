package team.floracore.common.util.nothing;

import java.lang.annotation.*;

/**
 * To visit a local variable(include arg)
 * You can change the variable if receive a WrappedObject
 * Otherwise,get the variable only
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.PARAMETER)
public @interface LocalVar {
    /**
     * The index of target variable
     * For non-static methods, 0 is this
     */
    int value();
}
