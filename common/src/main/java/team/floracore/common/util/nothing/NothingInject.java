package team.floracore.common.util.nothing;

import team.floracore.common.util.wrapper.WrappedObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sign an injecting
 * The injecting should return Optional&lt;T>, where T is return value of the method injected
 * Returning null means to continue running
 * Returning Optional.ofNullable(v) means forces the target method to return v or v.getRaw()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Repeatable(NothingInjects.class)
public @interface NothingInject {
	NothingPriority priority() default NothingPriority.NORMAL;

	/**
	 * All possible names of target method
	 * Only one method can be matched
	 */
	String[] name();

	/**
	 * Args types for matching method
	 *
	 * @see WrappedObject
	 */
	Class<?>[] args();

	NothingLocation location();

	/**
	 * @see NothingLocation
	 */
	NothingByteCode byteCode() default @NothingByteCode;

	/**
	 * For example, 1 means the next bytecode
	 */
	int shift() default 0;

	/**
	 * @see mz.lib.Optional
	 */
	boolean optional() default false;
}
