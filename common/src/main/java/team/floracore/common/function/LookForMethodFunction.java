package team.floracore.common.function;

import org.jetbrains.annotations.*;

import java.lang.reflect.*;

/**
 * @see team.floracore.common.util.ReflectionWrapper#findPossibleMethod(Class, LookForMethodFunction...)
 */
public interface LookForMethodFunction {
    @Nullable Method accept(@NotNull Class<?> cls) throws NoSuchMethodException;
}
