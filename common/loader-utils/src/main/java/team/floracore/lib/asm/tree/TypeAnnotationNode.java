package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.*;

/**
 * A node that represents a type annotation.
 *
 * @author Eric Bruneton
 */
public class TypeAnnotationNode extends AnnotationNode {

    /**
     * A reference to the annotated type. See {@link TypeReference}.
     */
    public int typeRef;

    /**
     * The path to the annotated type argument, wildcard bound, array element type, or static outer
     * type within the referenced type. May be {@literal null} if the annotation targets 'typeRef' as
     * a whole.
     */
    public TypePath typePath;

    /**
     * Constructs a new {@link AnnotationNode}. <i>Subclasses must not use this constructor</i>.
     * Instead, they must use the {@link #TypeAnnotationNode(int, int, TypePath, String)} version.
     *
     * @param typeRef    a reference to the annotated type. See {@link TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     *
     * @throws IllegalStateException If a subclass calls this constructor.
     */
    public TypeAnnotationNode(final int typeRef, final TypePath typePath, final String descriptor) {
        this(/* latest api = */ Opcodes.ASM9, typeRef, typePath, descriptor);
        if (getClass() != TypeAnnotationNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs a new {@link AnnotationNode}.
     *
     * @param api        the ASM API version implemented by this visitor. Must be one of the {@code
     *                   ASM}<i>x</i> values in {@link Opcodes}.
     * @param typeRef    a reference to the annotated type. See {@link TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     */
    public TypeAnnotationNode(
            final int api, final int typeRef, final TypePath typePath, final String descriptor) {
        super(api, descriptor);
        this.typeRef = typeRef;
        this.typePath = typePath;
    }
}
