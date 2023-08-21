package team.floracore.bukkit.util;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import team.floracore.bukkit.FCBukkitBootstrap;
import team.floracore.bukkit.util.module.AbsModule;
import team.floracore.bukkit.util.module.IRegistrar;
import team.floracore.bukkit.util.module.RegistrarRegistrar;
import team.floracore.bukkit.util.wrappednms.NmsEntityPlayer;
import team.floracore.bukkit.util.wrappednms.NmsPacket;
import team.floracore.bukkit.util.wrappedobc.ObcEntity;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.common.util.Ref;
import team.floracore.common.util.TypeUtil;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ProtocolUtil extends AbsModule implements IRegistrar<ProtocolUtil.PacketListener> {
    public static ProtocolUtil instance = new ProtocolUtil();
    public static List<Object> forcePackets = new LinkedList<>();
    static Map<Class<?>, List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>>> sendListeners = new HashMap<>();
    static Map<Class<?>, List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>>> receiveListeners = new HashMap<>();

    public ProtocolUtil() {
        super(FCBukkitBootstrap.loader, RegistrarRegistrar.instance);
    }

    public static void unregListener(TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>> listener) {
        for (Map<Class<?>, List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>,
                Class<? extends NmsPacket>>>> ls : Lists.newArrayList(
                sendListeners,
                receiveListeners)) {
            ls.forEach((t, i) -> i.remove(listener));
        }
    }

    public static boolean onPacketSend(Player receiver, NmsPacket packet) {
        if (forcePackets.remove(packet.getRaw())) {
            return false;
        }
        if (sendListeners.containsKey(packet.getRaw().getClass())) {
            Ref<Boolean> cancelled = new Ref<>(false);
            List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>> l =
                    sendListeners.get(
                            packet.getRaw().getClass());
            for (EventPriority p : EventPriority.values()) {
                l.get(p.ordinal()).forEach((c, t) ->
                {
                    try {
                        NmsPacket tp = packet.cast(t);
                        c.accept(receiver, TypeUtil.cast(tp), cancelled);
                        packet.setRaw(tp.getRaw());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            }
            return cancelled.get();
        }
        return false;
    }

    public static boolean onPacketReceive(Player sender, NmsPacket packet) {
        if (receiveListeners.containsKey(packet.getRaw().getClass())) {
            Ref<Boolean> cancelled = new Ref<>(false);
            List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>> l =
                    receiveListeners.get(packet.getRaw().getClass());
            for (EventPriority p : EventPriority.values()) {
                l.get(p.ordinal()).forEach((c, t) ->
                {
                    try {
                        NmsPacket tp = packet.cast(t);
                        c.accept(sender, TypeUtil.cast(tp), cancelled);
                        packet.setRaw(tp.getRaw());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            }
            return cancelled.get();
        }
        return false;
    }

    public static void sendPacketToAllPlayers(NmsPacket packet) {
        for (Player po : Bukkit.getOnlinePlayers()) {
            sendPacket(po, packet);
        }
    }

    public static void sendPacket(Player receiver, NmsPacket packet) {
        if (BukkitWrapper.version >= 12) {
            WrappedObject.wrap(ObcEntity.class, receiver)
                    .getHandle()
                    .cast(NmsEntityPlayer.class)
                    .getPlayerConnection()
                    .getNetworkManager()
                    .sendPacket(packet);
        } else {
            WrappedObject.wrap(ObcEntity.class, receiver)
                    .getHandle()
                    .cast(NmsEntityPlayer.class)
                    .getPlayerConnection()
                    .sendPacket(packet);
        }
    }

    public static <T extends NmsPacket> void regSendListener(EventPriority priority,
                                                             Class<T> type,
                                                             TriConsumer<Player, T, Ref<Boolean>> listener) {
        List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>> l =
                sendListeners.get(
                        WrappedObject.getRawClass(type));
        if (l == null) {
            l = new ArrayList<>(EventPriority.values().length);
            for (int i = 0; i < EventPriority.values().length; i++) {
                l.add(new HashMap<>());
            }
            sendListeners.put(WrappedObject.getRawClass(type), l);
        }
        l.get(priority.ordinal()).put(listener, type);
    }

    public static <T extends NmsPacket> void regReceiveListener(EventPriority priority,
                                                                Class<T> type,
                                                                TriConsumer<Player, T, Ref<Boolean>> listener) {
        List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>> l =
                receiveListeners.get(
                        WrappedObject.getRawClass(type));
        if (l == null) {
            l = new ArrayList<>();
            for (int i = 0; i < EventPriority.values().length; i++) {
                l.add(new HashMap<>());
            }
            receiveListeners.put(WrappedObject.getRawClass(type), l);
        }
        l.get(priority.ordinal()).put(listener, type);
    }

    public static <T extends NmsPacket> void unregSendListener(EventPriority priority,
                                                               Class<T> type,
                                                               TriConsumer<Player, T, Ref<Boolean>> listener) {
        List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>> l =
                sendListeners.get(
                        WrappedObject.getRawClass(type));
        if (l != null) {
            l.remove(listener);
        }
    }

    public static <T extends NmsPacket> void unregReceiveListener(EventPriority priority,
                                                                  Class<T> type,
                                                                  TriConsumer<Player, T, Ref<Boolean>> listener) {
        List<Map<TriConsumer<Player, ? extends NmsPacket, Ref<Boolean>>, Class<? extends NmsPacket>>> l =
                receiveListeners.get(
                        WrappedObject.getRawClass(type));
        if (l != null) {
            l.remove(listener);
        }
    }

    @Override
    public Class<PacketListener> getType() {
        return PacketListener.class;
    }

    @Override
    public boolean register(PacketListener obj) {
        if (obj instanceof SendListener) {
            ProtocolUtil.regSendListener(obj.priority, obj.type, obj.listener);
        } else if (obj instanceof ReceiveListener) {
            ProtocolUtil.regReceiveListener(obj.priority, obj.type, obj.listener);
        } else {
            throw new IllegalArgumentException("Unknown class " + obj.getClass());
        }
        return true;
    }

    @Override
    public void unregister(PacketListener obj) {
        if (obj instanceof SendListener) {
            ProtocolUtil.unregSendListener(obj.priority, obj.type, obj.listener);
        } else if (obj instanceof ReceiveListener) {
            ProtocolUtil.unregReceiveListener(obj.priority, obj.type, obj.listener);
        } else {
            throw new IllegalArgumentException("Unknown class " + obj.getClass());
        }
    }

    public static abstract class PacketListener<T extends NmsPacket> {
        public EventPriority priority;
        public Class<T> type;
        public TriConsumer<Player, T, Ref<Boolean>> listener;

        public PacketListener(EventPriority priority, Class<T> type, TriConsumer<Player, T, Ref<Boolean>> listener) {
            this.priority = priority;
            this.type = type;
            this.listener = listener;
        }
    }

    public static class SendListener<T extends NmsPacket> extends PacketListener<T> {
        public SendListener(EventPriority priority, Class<T> type, TriConsumer<Player, T, Ref<Boolean>> listener) {
            super(priority, type, listener);
        }
    }

    public static class ReceiveListener<T extends NmsPacket> extends PacketListener<T> {
        public ReceiveListener(EventPriority priority, Class<T> type, TriConsumer<Player, T, Ref<Boolean>> listener) {
            super(priority, type, listener);
        }
    }
}
