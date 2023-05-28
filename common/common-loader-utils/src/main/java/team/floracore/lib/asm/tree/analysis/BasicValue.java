package team.floracore.lib.asm.tree.analysis;

import team.floracore.lib.asm.*;

/**
 * A {@link Value} that is represented with its type in a seven types type system. This type system
 * distinguishes the UNINITIALZED, INT, FLOAT, LONG, DOUBLE, REFERENCE and RETURNADDRESS types.
 *
 * @author Eric Bruneton
 */
public class BasicValue implements Value {

    /**
     * An uninitialized value.
     */
    public static final BasicValue UNINITIALIZED_VALUE = new BasicValue(null);

    /**
     * A byte, boolean, char, short, or int value.
     */
    public static final BasicValue INT_VALUE = new BasicValue(Type.INT_TYPE);

    /**
     * A float value.
     */
    public static final BasicValue FLOAT_VALUE = new BasicValue(Type.FLOAT_TYPE);

    /**
     * A long value.
     */
    public static final BasicValue LONG_VALUE = new BasicValue(Type.LONG_TYPE);

    /**
     * A double value.
     */
    public static final BasicValue DOUBLE_VALUE = new BasicValue(Type.DOUBLE_TYPE);

    /**
     * An object or array reference value.
     */
    public static final BasicValue REFERENCE_VALUE =
            new BasicValue(Type.getObjectType("java/lang/Object"));

    /**
     * A return address value (produced by a jsr instruction).
     */
    public static final BasicValue RETURNADDRESS_VALUE = new BasicValue(Type.VOID_TYPE);

    /**
     * The {@link Type} of this value, or {@literal null} for uninitialized values.
     */
    private final Type type;

    /**
     * Constructs a new {@link BasicValue} of the given type.
     *
     * @param type the value type.
     */
    public BasicValue(final Type type) {
        this.type = type;
    }

    /**
     * Returns the {@link Type} of this value.
     *
     * @return the {@link Type} of this value.
     */
    public Type getType() {
        return type;
    }

    @Override
    public int getSize() {
        return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
    }

    /**
     * Returns whether this value corresponds to an object or array reference.
     *
     * @return whether this value corresponds to an object or array reference.
     */
    public boolean isReference() {
        return type != null && (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY);
    }

    @Override
    public int hashCode() {
        return type == null ? 0 : type.hashCode();
    }

    @Override
    public boolean equals(final Object value) {
        if (value == this) {
            return true;
        } else if (value instanceof BasicValue) {
            if (type == null) {
                return ((BasicValue) value).type == null;
            } else {
                return type.equals(((BasicValue) value).type);
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this == UNINITIALIZED_VALUE) {
            return ".";
        } else if (this == RETURNADDRESS_VALUE) {
            return "A";
        } else if (this == REFERENCE_VALUE) {
            return "R";
        } else {
            return type.getDescriptor();
        }
    }
}
