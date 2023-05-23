package team.floracore.common.util.nothing;

import java.lang.annotation.*;

/**
 * Take out value at current stack top, then put it back later
 * You can receive WrappedObject to change it
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.PARAMETER)
public @interface StackTop {
	/**
	 * The value type at the current stack top
	 * In addition to primitive type, you can use its wrapper class instead(Class&lt;? extends WrappedObject>)
	 */
	Class<?> value();
}
