package team.floracore.bukkit.util;

import io.github.karlatemp.unsafeaccessor.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.util.module.*;
import team.floracore.common.util.*;

import java.lang.reflect.*;
import java.util.*;

public class ListenerRegistrar extends AbsModule implements IRegistrar<Listener> {
    public static ListenerRegistrar instance = new ListenerRegistrar();

    public ListenerRegistrar() {
        super(FCBukkitBootstrap.loader, RegistrarRegistrar.instance);
    }

    @Override
    public Class<Listener> getType() {
        return Listener.class;
    }

    @Override
    public boolean register(IModule module, Listener obj) {
        try {
            for (Method method : obj.getClass().getDeclaredMethods()) {
                if (!method.isBridge() && !method.isSynthetic() && method.getParameterCount() == 1) {
                    EventHandler eventHandler = method.getDeclaredAnnotation(EventHandler.class);
                    if (eventHandler != null) {
                        Class<? extends Event> eventType = TypeUtil.cast(method.getParameterTypes()[0]);
                        if (Event.class.isAssignableFrom(eventType)) {
                            Method getHandlerList = eventType.getDeclaredMethod("getHandlerList");
                            Root.setAccessible(getHandlerList, true);
                            HandlerList handlerList = (HandlerList) getHandlerList.invoke(null);
                            Root.setAccessible(method, true);
                            handlerList.register(new RegisteredListener(obj, (listener, event) ->
                            {
                                try {
                                    method.invoke(listener, event);
                                } catch (Throwable e) {
                                    TypeUtil.throwException(e);
                                }
                            }, eventHandler.priority(), module.getPlugin(), eventHandler.ignoreCancelled()));
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw TypeUtil.throwException(e);
        }
        return true;
    }

    @Override
    public void unregister(Listener obj) {
        try {
            Set<HandlerList> handlerLists = new HashSet<>();
            for (Method method : obj.getClass().getDeclaredMethods()) {
                if (!method.isBridge() && !method.isSynthetic() && method.getParameterCount() == 1) {
                    if (method.getDeclaredAnnotation(EventHandler.class) != null) {
                        Class<? extends Event> eventType = TypeUtil.cast(method.getParameterTypes()[0]);
                        if (Event.class.isAssignableFrom(eventType)) {
                            Method getHandlerList = eventType.getDeclaredMethod("getHandlerList");
                            Root.setAccessible(getHandlerList, true);
                            handlerLists.add((HandlerList) getHandlerList.invoke(null));
                        }
                    }
                }
            }
            for (HandlerList handlerList : handlerLists) {
                handlerList.unregister(obj);
            }
        } catch (Throwable e) {
            throw TypeUtil.throwException(e);
        }
    }
}
