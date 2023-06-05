package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.Label;
import team.floracore.lib.asm.MethodVisitor;

import java.util.Map;

/**
 * An {@link AbstractInsnNode} that encapsulates a {@link Label}.
 */
public class LabelNode extends AbstractInsnNode {

    private Label value;

    public LabelNode() {
        super(-1);
    }

    public LabelNode(final Label label) {
        super(-1);
        this.value = label;
    }

    @Override
    public int getType() {
        return LABEL;
    }

    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitLabel(getLabel());
    }

    /**
     * Returns the label encapsulated by this node. A new label is created and associated with this
     * node if it was created without an encapsulated label.
     *
     * @return the label encapsulated by this node.
     */
    public Label getLabel() {
        if (value == null) {
            value = new Label();
        }
        return value;
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
        return clonedLabels.get(this);
    }

    public void resetLabel() {
        value = null;
    }
}
