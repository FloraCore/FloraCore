package team.floracore.lib.asm.tree.analysis;

import team.floracore.lib.asm.tree.AbstractInsnNode;

import java.util.Set;

/**
 * A {@link Value} which keeps track of the bytecode instructions that can produce it.
 *
 * @author Eric Bruneton
 */
public class SourceValue implements Value {

	/**
	 * The size of this value, in 32 bits words. This size is 1 for byte, boolean, char, short, int,
	 * float, object and array types, and 2 for long and double.
	 */
	public final int size;

	/**
	 * The instructions that can produce this value. For example, for the Java code below, the
	 * instructions that can produce the value of {@code i} at line 5 are the two ISTORE instructions
	 * at line 1 and 3:
	 *
	 * <pre>
	 * 1: i = 0;
	 * 2: if (...) {
	 * 3:   i = 1;
	 * 4: }
	 * 5: return i;
	 * </pre>
	 */
	public final Set<AbstractInsnNode> insns;

	/**
	 * Constructs a new {@link SourceValue}.
	 *
	 * @param size the size of this value, in 32 bits words. This size is 1 for byte, boolean, char,
	 *             short, int, float, object and array types, and 2 for long and double.
	 */
	public SourceValue(final int size) {
		this(size, new SmallSet<>());
	}

	/**
	 * Constructs a new {@link SourceValue}.
	 *
	 * @param size     the size of this value, in 32 bits words. This size is 1 for byte, boolean, char,
	 *                 short, int, float, object and array types, and 2 for long and double.
	 * @param insnNode an instruction that can produce this value.
	 */
	public SourceValue(final int size, final AbstractInsnNode insnNode) {
		this.size = size;
		this.insns = new SmallSet<>(insnNode);
	}

	/**
	 * Constructs a new {@link SourceValue}.
	 *
	 * @param size    the size of this value, in 32 bits words. This size is 1 for byte, boolean, char,
	 *                short, int, float, object and array types, and 2 for long and double.
	 * @param insnSet the instructions that can produce this value.
	 */
	public SourceValue(final int size, final Set<AbstractInsnNode> insnSet) {
		this.size = size;
		this.insns = insnSet;
	}

	/**
	 * Returns the size of this value.
	 *
	 * @return the size of this value, in 32 bits words. This size is 1 for byte, boolean, char,
	 * short, int, float, object and array types, and 2 for long and double.
	 */
	@Override
	public int getSize() {
		return size;
	}

	@Override
	public boolean equals(final Object value) {
		if (!(value instanceof SourceValue)) {
			return false;
		}
		SourceValue sourceValue = (SourceValue) value;
		return size == sourceValue.size && insns.equals(sourceValue.insns);
	}

	@Override
	public int hashCode() {
		return insns.hashCode();
	}
}
