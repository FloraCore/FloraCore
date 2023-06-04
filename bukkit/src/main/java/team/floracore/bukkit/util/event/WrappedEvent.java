package team.floracore.bukkit.util.event;

import org.bukkit.event.Event;
import team.floracore.common.util.wrapper.WrappedClass;
import team.floracore.common.util.wrapper.WrappedFieldAccessor;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedClass("org.bukkit.event.Event")
public interface WrappedEvent extends WrappedObject {
	@Override
	Event getRaw();

	@WrappedFieldAccessor("async")
	WrappedEvent setAsync(boolean async);
}
