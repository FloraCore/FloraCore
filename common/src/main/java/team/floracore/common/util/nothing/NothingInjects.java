package team.floracore.common.util.nothing;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface NothingInjects {
	NothingInject[] value();
}
