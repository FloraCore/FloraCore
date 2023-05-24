package team.floracore.bukkit.util.nothing;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface NothingBukkitInjects {
    NothingBukkitInject[] value();
}
