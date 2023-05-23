package team.floracore.bukkit.util.wrapper;

import team.floracore.bukkit.util.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target(METHOD)
public @interface WrappedBukkitMethod {
	VersionName[] value();
}
