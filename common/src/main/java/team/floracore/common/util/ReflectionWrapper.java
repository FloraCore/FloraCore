package team.floracore.common.util;

import io.github.karlatemp.unsafeaccessor.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.lang.reflect.*;
import java.util.function.*;

/**
 * nms相关操作
 * 不强制try/catch的反射操作
 */
public final class ReflectionWrapper {
    public static final Method sendPacket = getMethod(getNMSClass("PlayerConnection"), "sendPacket", getNMSClass("Packet"));
    public static final Field playerConnection = getField(getNMSClass("EntityPlayer"), "playerConnection");
    public static final Method getHandle = getMethod(getCraftBukkitClass("entity.CraftPlayer"), "getHandle");

    private ReflectionWrapper() {
    }

    public static <T> T newInstance(final Constructor<T> con, final Object... args) {
        try {
            Root.setAccessible(con, true);
            return con.newInstance(args);
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getClassByName(final String n) {
        try {
            return Class.forName(n);
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static Method getMethodParent(final Class<?> c, final String method, final Class<?>... args) {
        try {
            try {
                return ReflectionWrapper.getMethod(c, method, args);
            } catch (final Throwable e) {
                if (c.getSuperclass() != null) {
                    return ReflectionWrapper.getMethodParent(c.getSuperclass(), method, args);
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        } catch (final Throwable e) {
            final Class<?>[] classes = c.getInterfaces();
            Throwable lastExc = null;
            for (final Class<?> i : classes) {
                Throwable exc = null;
                try {
                    return ReflectionWrapper.getMethodParent(i, method, args);
                } catch (final Throwable e2) {
                    exc = e2;
                    // ignore
                }
                lastExc = exc;
            }
            if (lastExc != null) {
                if (lastExc instanceof RuntimeException) {
                    throw (RuntimeException) lastExc;
                } else {
                    throw new RuntimeException(e);
                }
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object getCraftPlayer(Player p) {
        Class<?> clazz = getCraftBukkitClass("entity.CraftPlayer");
        return clazz.cast(p);
    }

    public static Field getFieldParent(final Class<?> c, final String name) {
        try {
            try {
                return ReflectionWrapper.getField(c, name);
            } catch (final Throwable e) {
                if (c.getSuperclass() != null) {
                    return ReflectionWrapper.getFieldParent(c.getSuperclass(), name);
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        } catch (final Throwable e) {
            final Class<?>[] classes = c.getInterfaces();
            Throwable lastExc = null;
            for (final Class<?> i : classes) {
                Throwable exc = null;
                try {
                    return ReflectionWrapper.getFieldParent(i, name);
                } catch (final Throwable e2) {
                    exc = e2;
                    // ignore
                }
                lastExc = exc;
            }
            if (lastExc != null) {
                if (lastExc instanceof RuntimeException) {
                    throw (RuntimeException) lastExc;
                } else {
                    throw new RuntimeException(e);
                }
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> Constructor<T> getInnerConstructor(final Class<T> c, final Class<?>... args) {
        try {
            final Class<?>[] rargs = new Class<?>[args.length + 1];
            rargs[0] = c.getEnclosingClass();
            System.arraycopy(args, 0, rargs, 1, rargs.length - 1);
            final Constructor<T> con = c.getDeclaredConstructor(rargs);
            Root.setAccessible(con, true);
            return con;
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static <T> Constructor<T> getConstructor(final Class<T> c, final Class<?>... args) {
        try {
            final Constructor<T> con = c.getDeclaredConstructor(args);
            Root.setAccessible(con, true);
            return con;
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getInnerClass(final Class<?> parent, final String name) {
        final Class<?>[] classes = parent.getDeclaredClasses();
        for (final Class<?> i : classes) {
            if (i.getSimpleName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    public static Method getMethod(final Class<?> c, final String n, final Class<?>... t) {
        try {
            final Method m = c.getDeclaredMethod(n, t);
            Root.setAccessible(m, true);
            return m;
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(final Method m, final Object o, final Object... args) {
        try {
            Root.setAccessible(m, true);
            return (T) m.invoke(o, args);
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static <T> T invokeStaticMethod(final Method m, final Object... args) {
        Root.setAccessible(m, true);
        return ReflectionWrapper.invokeMethod(m, null, args);
    }

    public static String getNMSClassName(final String c) {
        return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "net.minecraft.server") + "." + c;
    }

    public static Class<?> getNMSClass(final String c) {
        try {
            return Class.forName(getNMSClassName(c));
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getCraftBukkitClass(final String c) {
        try {
            return Class.forName(Bukkit.getServer().getClass().getPackage().getName() + "." + c);
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static Field getField(final Class<?> c, final String n) {
        try {
            final Field f = c.getDeclaredField(n);
            Root.setAccessible(f, true);
            return f;
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "restriction"})
    public static <T> T getFieldValue(final Field f, final Object o) {
        if (unsafe == null) {
            Root.setAccessible(f, true);
            try {
                return (T) f.get(o);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        boolean isStatic = Modifier.isStatic(f.getModifiers());
        boolean isVolatile = Modifier.isVolatile(f.getModifiers());
        if (o == null && !isStatic) {
            throw new NullPointerException();
        }
        long offset = isStatic ? unsafe.staticFieldOffset(f) : unsafe.objectFieldOffset(f);
        if (f.getType().isPrimitive()) {
            if (f.getType() == int.class) {
                if (isVolatile) {
                    return (T) (Integer) unsafe.getIntVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Integer) unsafe.getInt(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else if (f.getType() == float.class) {
                if (isVolatile) {
                    return (T) (Float) unsafe.getFloatVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Float) unsafe.getFloat(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else if (f.getType() == double.class) {
                if (isVolatile) {
                    return (T) (Double) unsafe.getDoubleVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Double) unsafe.getDouble(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else if (f.getType() == boolean.class) {
                if (isVolatile) {
                    return (T) (Boolean) unsafe.getBooleanVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Boolean) unsafe.getBoolean(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else if (f.getType() == byte.class) {
                if (isVolatile) {
                    return (T) (Byte) unsafe.getByteVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Byte) unsafe.getByte(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else if (f.getType() == char.class) {
                if (isVolatile) {
                    return (T) (Character) unsafe.getCharVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Character) unsafe.getChar(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else if (f.getType() == long.class) {
                if (isVolatile) {
                    return (T) (Long) unsafe.getLongVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Long) unsafe.getLong(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else if (f.getType() == short.class) {
                if (isVolatile) {
                    return (T) (Short) unsafe.getShortVolatile(isStatic ? f.getDeclaringClass() : o, offset);
                } else {
                    return (T) (Short) unsafe.getShort(isStatic ? f.getDeclaringClass() : o, offset);
                }
            } else {
                return null;
            }
        } else {
            if (isVolatile) {
                return (T) unsafe.getReferenceVolatile(isStatic ? f.getDeclaringClass() : o, offset);
            } else {
                return (T) unsafe.getReference(isStatic ? f.getDeclaringClass() : o, offset);
            }
        }
    }

    public static <T> T getStaticFieldValue(final Field f) {
        return ReflectionWrapper.getFieldValue(f, null);
    }

    @SuppressWarnings("restriction")
    public static <T> T setFieldValue(final Field f, final Object o, final T v) {
        boolean isStatic = Modifier.isStatic(f.getModifiers());
        boolean isVolatile = Modifier.isVolatile(f.getModifiers());
        if (o == null && !isStatic) {
            throw new NullPointerException();
        }
        long offset = isStatic ? unsafe.staticFieldOffset(f) : unsafe.objectFieldOffset(f);
        if (f.getType().isPrimitive()) {
            if (f.getType() == int.class) {
                if (isVolatile) {
                    unsafe.putIntVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Integer) v);
                } else {
                    unsafe.putInt(isStatic ? f.getDeclaringClass() : o, offset, (Integer) v);
                }
            } else if (f.getType() == float.class) {
                if (isVolatile) {
                    unsafe.putFloatVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Float) v);
                } else {
                    unsafe.putFloat(isStatic ? f.getDeclaringClass() : o, offset, (Float) v);
                }
            } else if (f.getType() == double.class) {
                if (isVolatile) {
                    unsafe.putDoubleVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Double) v);
                } else {
                    unsafe.putDouble(isStatic ? f.getDeclaringClass() : o, offset, (Double) v);
                }
            } else if (f.getType() == boolean.class) {
                if (isVolatile) {
                    unsafe.putBooleanVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Boolean) v);
                } else {
                    unsafe.putBoolean(isStatic ? f.getDeclaringClass() : o, offset, (Boolean) v);
                }
            } else if (f.getType() == byte.class) {
                if (isVolatile) {
                    unsafe.putByteVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Byte) v);
                } else {
                    unsafe.putByte(isStatic ? f.getDeclaringClass() : o, offset, (Byte) v);
                }
            } else if (f.getType() == char.class) {
                if (isVolatile) {
                    unsafe.putCharVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Character) v);
                } else {
                    unsafe.putChar(isStatic ? f.getDeclaringClass() : o, offset, (Character) v);
                }
            } else if (f.getType() == long.class) {
                if (isVolatile) {
                    unsafe.putLongVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Long) v);
                } else {
                    unsafe.putLong(isStatic ? f.getDeclaringClass() : o, offset, (Long) v);
                }
            } else if (f.getType() == short.class) {
                if (isVolatile) {
                    unsafe.putShortVolatile(isStatic ? f.getDeclaringClass() : o, offset, (Short) v);
                } else {
                    unsafe.putShort(isStatic ? f.getDeclaringClass() : o, offset, (Short) v);
                }
            }
        } else {
            if (isVolatile) {
                unsafe.putReferenceVolatile(isStatic ? f.getDeclaringClass() : o, offset, f.getType().cast(v));
            } else {
                unsafe.putReference(isStatic ? f.getDeclaringClass() : o, offset, f.getType().cast(v));
            }
        }
        return v;
    }

    public static <T> T setStaticFieldValue(final Field f, final T v) {
        return ReflectionWrapper.setFieldValue(f, null, v);
    }

    public static void copyObjectData(Class<?> clazz, Object src, Object tar) {
        Class<?> s = clazz.getSuperclass();
        if (s != Object.class) {
            copyObjectData(s, src, tar);
        }
        for (Field f : clazz.getFields()) {
            if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
                setFieldValue(f, tar, getFieldValue(f, src));
            }
        }
    }

    public static void sendPacket(Player p, Object packet) {
        Object NMSPlayer = invokeMethod(getHandle, p);
        Object con = getFieldValue(playerConnection, NMSPlayer);
        invokeMethod(sendPacket, con, packet);
    }

    public static void sendPacketToAllPlayers(Object pack) {
        for (Player po : Bukkit.getOnlinePlayers()) {
            sendPacket(po, pack);
        }
    }

    public static void sendPacketToAllPlayersWhich(Object pack, Predicate<Player> con) {
        for (Player po : Bukkit.getOnlinePlayers()) {
            if (con.test(po)) sendPacket(po, pack);
        }
    }


    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static boolean isVersionGreaterThan(String version, String compareVersion) {
        // 分解版本号字符串
        String[] parts1 = version.substring(1).split("_");
        String[] parts2 = compareVersion.substring(1).split("_");

        // 解析主版本号、次版本号和修订版
        int major1 = Integer.parseInt(parts1[0]);
        int minor1 = Integer.parseInt(parts1[1]);
        String revision1 = parts1[2];

        int major2 = Integer.parseInt(parts2[0]);
        int minor2 = Integer.parseInt(parts2[1]);
        String revision2 = parts2[2];

        // 比较版本号
        if (major1 > major2) {
            return true;
        } else if (major1 < major2) {
            return false;
        } else {
            // 主版本号相同，比较次版本号
            if (minor1 > minor2) {
                return true;
            } else if (minor1 < minor2) {
                return false;
            } else {
                // 次版本号也相同，比较修订版
                return revision1.compareTo(revision2) > 0;
            }
        }
    }

    public static boolean isVersionGreaterThanOrEqual(String version, String compareVersion) {
        return isVersionGreaterThan(version, compareVersion) || version.equals(compareVersion);
    }


    public static Unsafe unsafe = getStaticFieldValue(getField(Unsafe.class, "theUnsafe"));
}
