package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.*;

import java.util.*;

/**
 * A node that represents an invokedynamic instruction.
 *
 * @author Remi Forax
 */
public class InvokeDynamicInsnNode extends AbstractInsnNode {

    /**
     * The method's name.
     */
    public String name;

    /**
     * The method's descriptor (see {@link Type}).
     */
    public String desc;

    /**
     * The bootstrap method.
     */
    public Handle bsm;

    /**
     * The bootstrap method constant arguments.
     */
    public Object[] bsmArgs;

    /**
     * Constructs a new {@link InvokeDynamicInsnNode}.
     *
     * @param name                     the method's name.
     * @param descriptor               the method's descriptor (see {@link Type}).
     * @param bootstrapMethodHandle    the bootstrap method.
     * @param bootstrapMethodArguments the bootstrap method constant arguments. Each argument must be
     *                                 an {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String}, {@link
     *                                 Type} or {@link Handle} value. This method is allowed to modify the
     *                                 content of the array so a caller should expect that this array may change.
     */
    public InvokeDynamicInsnNode(
            final String name,
            final String descriptor,
            final Handle bootstrapMethodHandle,
            final Object... bootstrapMethodArguments) {
        super(Opcodes.INVOKEDYNAMIC);
        this.name = name;
        this.desc = descriptor;
        this.bsm = bootstrapMethodHandle;
        this.bsmArgs = bootstrapMethodArguments; // NOPMD(ArrayIsStoredDirectly): public field.
    }

    @Override
    public int getType() {
        return INVOKE_DYNAMIC_INSN;
    }

    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
        return new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs).cloneAnnotations(this);
    }
}
