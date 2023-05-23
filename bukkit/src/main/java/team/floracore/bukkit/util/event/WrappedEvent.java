package team.floracore.bukkit.util.event;

import org.bukkit.event.*;
import team.floracore.common.util.wrapper.*;

@WrappedClass("org.bukkit.event.Event")
public interface WrappedEvent extends WrappedObject {
    @Override
    Event getRaw();

    @WrappedFieldAccessor("async")
    WrappedEvent setAsync(boolean async);
}
