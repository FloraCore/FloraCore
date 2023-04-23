package team.floracore.common.util;

import io.github.karlatemp.unsafeaccessor.*;
import org.bukkit.*;

import java.lang.reflect.*;

/**
 * nms相关操作
 * 不强制try/catch的反射操作
 */
public final class ReflectionWrapper {
    private ReflectionWrapper() {
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
