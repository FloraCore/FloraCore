package team.floracore.bukkit.util.nothing;

import team.floracore.bukkit.util.VersionName;
import team.floracore.common.util.Optional;
import team.floracore.common.util.nothing.NothingLocation;
import team.floracore.common.util.nothing.NothingPriority;
import team.floracore.common.util.wrapper.WrappedObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Repeatable(NothingBukkitInjects.class)
public @interface NothingBukkitInject {
	NothingPriority priority() default NothingPriority.NORMAL;

	/**
	 * All possible names of target method
	 * Only one method can be matched
	 */
	VersionName[] name();

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
	NothingBukkitByteCode byteCode() default @NothingBukkitByteCode;

	/**
	 * For example, 1 means the next bytecode
	 */
	int shift() default 0;

	/**
	 * @see Optional
	 */
	boolean optional() default false;
}
