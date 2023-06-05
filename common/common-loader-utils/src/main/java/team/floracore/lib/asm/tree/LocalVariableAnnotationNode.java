
package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.*;

import java.util.List;

/**
 * A node that represents a type annotation on a local or resource variable.
 *
 * @author Eric Bruneton
 */
public class LocalVariableAnnotationNode extends TypeAnnotationNode {

    /**
     * The fist instructions corresponding to the continuous ranges that make the scope of this local
     * variable (inclusive). Must not be {@literal null}.
     */
    public List<LabelNode> start;

    /**
     * The last instructions corresponding to the continuous ranges that make the scope of this local
     * variable (exclusive). This list must have the same size as the 'start' list. Must not be
     * {@literal null}.
     */
    public List<LabelNode> end;

    /**
     * The local variable's index in each range. This list must have the same size as the 'start'
     * list. Must not be {@literal null}.
     */
    public List<Integer> index;

    /**
     * Constructs a new {@link LocalVariableAnnotationNode}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the {@link #LocalVariableAnnotationNode(int, TypePath,
     * LabelNode[], LabelNode[], int[], String)} version.
     *
     * @param typeRef    a reference to the annotated type. See {@link TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param start      the fist instructions corresponding to the continuous ranges that make the scope
     *                   of this local variable (inclusive).
     * @param end        the last instructions corresponding to the continuous ranges that make the scope of
     *                   this local variable (exclusive). This array must have the same size as the 'start' array.
     * @param index      the local variable's index in each range. This array must have the same size as
     *                   the 'start' array.
     * @param descriptor the class descriptor of the annotation class.
     */
    public LocalVariableAnnotationNode(
            final int typeRef,
            final TypePath typePath,
            final LabelNode[] start,
            final LabelNode[] end,
            final int[] index,
            final String descriptor) {
        this(/* latest api = */ Opcodes.ASM9, typeRef, typePath, start, end, index, descriptor);
    }

    /**
     * Constructs a new {@link LocalVariableAnnotationNode}.
     *
     * @param api        the ASM API version implemented by this visitor. Must be one of the {@code
     *                   ASM}<i>x</i> values in {@link Opcodes}.
     * @param typeRef    a reference to the annotated type. See {@link TypeReference}.
     * @param start      the fist instructions corresponding to the continuous ranges that make the scope
     *                   of this local variable (inclusive).
     * @param end        the last instructions corresponding to the continuous ranges that make the scope of
     *                   this local variable (exclusive). This array must have the same size as the 'start' array.
     * @param index      the local variable's index in each range. This array must have the same size as
     *                   the 'start' array.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     */
    public LocalVariableAnnotationNode(
            final int api,
            final int typeRef,
            final TypePath typePath,
            final LabelNode[] start,
            final LabelNode[] end,
            final int[] index,
            final String descriptor) {
        super(api, typeRef, typePath, descriptor);
        this.start = Util.asArrayList(start);
        this.end = Util.asArrayList(end);
        this.index = Util.asArrayList(index);
    }

    /**
     * Makes the given visitor visit this type annotation.
     *
     * @param methodVisitor the visitor that must visit this annotation.
     * @param visible       {@literal true} if the annotation is visible at runtime.
     */
    public void accept(final MethodVisitor methodVisitor, final boolean visible) {
        Label[] startLabels = new Label[this.start.size()];
        Label[] endLabels = new Label[this.end.size()];
        int[] indices = new int[this.index.size()];
        for (int i = 0, n = startLabels.length; i < n; ++i) {
            startLabels[i] = this.start.get(i).getLabel();
            endLabels[i] = this.end.get(i).getLabel();
            indices[i] = this.index.get(i);
        }
        accept(
                methodVisitor.visitLocalVariableAnnotation(
                        typeRef, typePath, startLabels, endLabels, indices, desc, visible));
    }
}
