package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.MethodVisitor;

import java.util.Map;

/**
 * A node that represents an instruction with a single int operand.
 *
 * @author Eric Bruneton
 */
public class IntInsnNode extends AbstractInsnNode {

	/**
	 * The operand of this instruction.
	 */
	public int operand;

	/**
	 * Constructs a new {@link IntInsnNode}.
	 *
	 * @param opcode  the opcode of the instruction to be constructed. This opcode must be BIPUSH,
	 *                SIPUSH or NEWARRAY.
	 * @param operand the operand of the instruction to be constructed.
	 */
	public IntInsnNode(final int opcode, final int operand) {
		super(opcode);
		this.operand = operand;
	}

	/**
	 * Sets the opcode of this instruction.
	 *
	 * @param opcode the new instruction opcode. This opcode must be BIPUSH, SIPUSH or NEWARRAY.
	 */
	public void setOpcode(final int opcode) {
		this.opcode = opcode;
	}

	@Override
	public int getType() {
		return INT_INSN;
	}

	@Override
	public void accept(final MethodVisitor methodVisitor) {
		methodVisitor.visitIntInsn(opcode, operand);
		acceptAnnotations(methodVisitor);
	}

	@Override
	public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
		return new IntInsnNode(opcode, operand).cloneAnnotations(this);
	}
}
