package team.floracore.lib.asm.tree.analysis;

/**
 * An immutable symbolic value for the semantic interpretation of bytecode.
 *
 * @author Eric Bruneton
 */
public interface Value {

	/**
	 * Returns the size of this value in 32 bits words. This size should be 1 for byte, boolean, char,
	 * short, int, float, object and array types, and 2 for long and double.
	 *
	 * @return either 1 or 2.
	 */
	int getSize();
}
