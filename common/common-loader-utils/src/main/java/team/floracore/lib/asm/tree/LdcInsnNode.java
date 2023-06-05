package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.*;

import java.util.Map;

/**
 * A node that represents an LDC instruction.
 *
 * @author Eric Bruneton
 */
public class LdcInsnNode extends AbstractInsnNode {

    /**
     * The constant to be loaded on the stack. This field must be a non null {@link Integer}, a {@link
     * Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link Type} of OBJECT or ARRAY
     * sort for {@code .class} constants, for classes whose version is 49, a {@link Type} of METHOD
     * sort for MethodType, a {@link Handle} for MethodHandle constants, for classes whose version is
     * 51 or a {@link ConstantDynamic} for a constant dynamic for classes whose version is 55.
     */
    public Object cst;

    /**
     * Constructs a new {@link LdcInsnNode}.
     *
     * @param value the constant to be loaded on the stack. This parameter mist be a non null {@link
     *              Integer}, a {@link Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link
     *              Type} of OBJECT or ARRAY sort for {@code .class} constants, for classes whose version is
     *              49, a {@link Type} of METHOD sort for MethodType, a {@link Handle} for MethodHandle
     *              constants, for classes whose version is 51 or a {@link ConstantDynamic} for a constant
     *              dynamic for classes whose version is 55.
     */
    public LdcInsnNode(final Object value) {
        super(Opcodes.LDC);
        this.cst = value;
    }

    @Override
    public int getType() {
        return LDC_INSN;
    }

    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(cst);
        acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
        return new LdcInsnNode(cst).cloneAnnotations(this);
    }
}
