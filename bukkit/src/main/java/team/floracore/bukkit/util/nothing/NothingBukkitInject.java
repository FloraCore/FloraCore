package team.floracore.bukkit.util.nothing;

import team.floracore.bukkit.util.*;
import team.floracore.common.util.nothing.*;
import team.floracore.common.util.wrapper.*;

import java.lang.annotation.*;

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
     * @see team.floracore.common.util.Optional
     */
    boolean optional() default false;
}
