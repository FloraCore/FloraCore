package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.MethodVisitor;
import team.floracore.lib.asm.Opcodes;

/**
 * A node that represents a parameter of a method.
 *
 * @author Remi Forax
 */
public class ParameterNode {

    /**
     * The parameter's name.
     */
    public String name;

    /**
     * The parameter's access flags (see {@link Opcodes}). Valid values are {@code
     * ACC_FINAL}, {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     */
    public int access;

    /**
     * Constructs a new {@link ParameterNode}.
     *
     * @param access The parameter's access flags. Valid values are {@code ACC_FINAL}, {@code
     *               ACC_SYNTHETIC} or/and {@code ACC_MANDATED} (see {@link Opcodes}).
     * @param name   the parameter's name.
     */
    public ParameterNode(final String name, final int access) {
        this.name = name;
        this.access = access;
    }

    /**
     * Makes the given visitor visit this parameter declaration.
     *
     * @param methodVisitor a method visitor.
     */
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitParameter(name, access);
    }
}
