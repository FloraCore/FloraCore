package team.floracore.common.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * @see team.floracore.common.util.ReflectionWrapper#findPossibleMethod(Class, LookForMethodFunction...)
 */
public interface LookForMethodFunction {
    @Nullable Method accept(@NotNull Class<?> cls) throws NoSuchMethodException;
}
