package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.*;

import java.util.*;

/**
 * A node that represents a local variable instruction. A local variable instruction is an
 * instruction that loads or stores the value of a local variable.
 *
 * @author Eric Bruneton
 */
public class VarInsnNode extends AbstractInsnNode {

    /**
     * The operand of this instruction. This operand is the index of a local variable.
     */
    public int var;

    /**
     * Constructs a new {@link VarInsnNode}.
     *
     * @param opcode   the opcode of the local variable instruction to be constructed. This opcode must
     *                 be ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param varIndex the operand of the instruction to be constructed. This operand is the index of
     *                 a local variable.
     */
    public VarInsnNode(final int opcode, final int varIndex) {
        super(opcode);
        this.var = varIndex;
    }

    /**
     * Sets the opcode of this instruction.
     *
     * @param opcode the new instruction opcode. This opcode must be ILOAD, LLOAD, FLOAD, DLOAD,
     *               ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     */
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return VAR_INSN;
    }

    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitVarInsn(opcode, var);
        acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
        return new VarInsnNode(opcode, var).cloneAnnotations(this);
    }
}
