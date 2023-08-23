package team.floracore.lib.asm;

import lombok.Getter;

/**
 * Exception thrown when the Code attribute of a method produced by a {@link ClassWriter} is too
 * large.
 *
 * @author Jason Zaugg
 */
@Getter
public final class MethodTooLargeException extends IndexOutOfBoundsException {
	private static final long serialVersionUID = 6807380416709738314L;

	/**
	 * -- GETTER --
	 * Returns the internal name of the owner class.
	 */
	private final String className;
	/**
	 * -- GETTER --
	 * Returns the name of the method.
	 */
	private final String methodName;
	/**
	 * -- GETTER --
	 * Returns the descriptor of the method.
	 */
	private final String descriptor;
	/**
	 * -- GETTER --
	 * Returns the size of the method's Code attribute, in bytes.
	 */
	private final int codeSize;

	/**
	 * Constructs a new {@link MethodTooLargeException}.
	 *
	 * @param className  the internal name of the owner class (see {@link Type#getInternalName()}).
	 * @param methodName the name of the method.
	 * @param descriptor the descriptor of the method.
	 * @param codeSize   the size of the method's Code attribute, in bytes.
	 */
	public MethodTooLargeException(
			final String className,
			final String methodName,
			final String descriptor,
			final int codeSize) {
		super("Method too large: " + className + "." + methodName + " " + descriptor);
		this.className = className;
		this.methodName = methodName;
		this.descriptor = descriptor;
		this.codeSize = codeSize;
	}

}
