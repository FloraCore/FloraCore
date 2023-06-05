package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.MethodVisitor;
import team.floracore.lib.asm.Type;

import java.util.Map;

/**
 * A node that represents a field instruction. A field instruction is an instruction that loads or
 * stores the value of a field of an object.
 *
 * @author Eric Bruneton
 */
public class FieldInsnNode extends AbstractInsnNode {

    /**
     * The internal name of the field's owner class (see {@link
     * Type#getInternalName()}).
     */
    public String owner;

    /**
     * The field's name.
     */
    public String name;

    /**
     * The field's descriptor (see {@link Type}).
     */
    public String desc;

    /**
     * Constructs a new {@link FieldInsnNode}.
     *
     * @param opcode     the opcode of the type instruction to be constructed. This opcode must be
     *                   GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner      the internal name of the field's owner class (see {@link
     *                   Type#getInternalName()}).
     * @param name       the field's name.
     * @param descriptor the field's descriptor (see {@link Type}).
     */
    public FieldInsnNode(
            final int opcode, final String owner, final String name, final String descriptor) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = descriptor;
    }

    /**
     * Sets the opcode of this instruction.
     *
     * @param opcode the new instruction opcode. This opcode must be GETSTATIC, PUTSTATIC, GETFIELD or
     *               PUTFIELD.
     */
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return FIELD_INSN;
    }

    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitFieldInsn(opcode, owner, name, desc);
        acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
        return new FieldInsnNode(opcode, owner, name, desc).cloneAnnotations(this);
    }
}
