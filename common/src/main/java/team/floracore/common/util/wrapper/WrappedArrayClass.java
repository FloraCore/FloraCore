package team.floracore.common.util.wrapper;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target(TYPE)
public @interface WrappedArrayClass {
    Class<? extends WrappedObject> value();
}
