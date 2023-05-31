package team.floracore.lib.asm.tree.analysis;

import team.floracore.lib.asm.tree.AbstractInsnNode;

/**
 * An exception thrown if a problem occurs during the analysis of a method.
 *
 * @author Bing Ran
 * @author Eric Bruneton
 */
public class AnalyzerException extends Exception {

    private static final long serialVersionUID = 3154190448018943333L;

    /**
     * The bytecode instruction where the analysis failed.
     */
    public final transient AbstractInsnNode node;

    /**
     * Constructs a new {@link AnalyzerException}.
     *
     * @param insn    the bytecode instruction where the analysis failed.
     * @param message the reason why the analysis failed.
     */
    public AnalyzerException(final AbstractInsnNode insn, final String message) {
        super(message);
        this.node = insn;
    }

    /**
     * Constructs a new {@link AnalyzerException}.
     *
     * @param insn    the bytecode instruction where the analysis failed.
     * @param message the reason why the analysis failed.
     * @param cause   the cause of the failure.
     */
    public AnalyzerException(
            final AbstractInsnNode insn, final String message, final Throwable cause) {
        super(message, cause);
        this.node = insn;
    }

    /**
     * Constructs a new {@link AnalyzerException}.
     *
     * @param insn     the bytecode instruction where the analysis failed.
     * @param message  the reason why the analysis failed.
     * @param expected an expected value.
     * @param actual   the actual value, different from the expected one.
     */
    public AnalyzerException(
            final AbstractInsnNode insn,
            final String message,
            final Object expected,
            final Value actual) {
        super(
                (message == null ? "Expected " : message + ": expected ")
                        + expected
                        + ", but found "
                        + actual);
        this.node = insn;
    }
}
