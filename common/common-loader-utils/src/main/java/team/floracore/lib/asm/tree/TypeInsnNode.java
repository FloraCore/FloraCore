package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.MethodVisitor;
import team.floracore.lib.asm.Type;

import java.util.Map;

/**
 * A node that represents a type instruction. A type instruction is an instruction which takes an
 * internal name as parameter (see {@link Type#getInternalName()}).
 *
 * @author Eric Bruneton
 */
public class TypeInsnNode extends AbstractInsnNode {

    /**
     * The operand of this instruction. Despite its name (due to historical reasons), this operand is
     * an internal name (see {@link Type#getInternalName()}).
     */
    public String desc;

    /**
     * Constructs a new {@link TypeInsnNode}.
     *
     * @param opcode the opcode of the type instruction to be constructed. This opcode must be NEW,
     *               ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param type   the operand of the instruction to be constructed. This operand is an internal name
     *               (see {@link Type#getInternalName()}).
     */
    public TypeInsnNode(final int opcode, final String type) {
        super(opcode);
        this.desc = type;
    }

    /**
     * Sets the opcode of this instruction.
     *
     * @param opcode the new instruction opcode. This opcode must be NEW, ANEWARRAY, CHECKCAST or
     *               INSTANCEOF.
     */
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return TYPE_INSN;
    }

    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitTypeInsn(opcode, desc);
        acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
        return new TypeInsnNode(opcode, desc).cloneAnnotations(this);
    }
}
