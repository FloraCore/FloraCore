
package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.AnnotationVisitor;
import team.floracore.lib.asm.FieldVisitor;
import team.floracore.lib.asm.Opcodes;
import team.floracore.lib.asm.TypePath;

/**
 * A {@link FieldVisitor} that remaps types with a {@link Remapper}.
 *
 * @author Eugene Kuleshov
 */
public class FieldRemapper extends FieldVisitor {

    /**
     * The remapper used to remap the types in the visited field.
     */
    protected final Remapper remapper;

    /**
     * Constructs a new {@link FieldRemapper}. <i>Subclasses must not use this constructor</i>.
     * Instead, they must use the {@link #FieldRemapper(int, FieldVisitor, Remapper)} version.
     *
     * @param fieldVisitor the field visitor this remapper must delegate to.
     * @param remapper     the remapper to use to remap the types in the visited field.
     */
    public FieldRemapper(final FieldVisitor fieldVisitor, final Remapper remapper) {
        this(/* latest api = */ Opcodes.ASM9, fieldVisitor, remapper);
    }

    /**
     * Constructs a new {@link FieldRemapper}.
     *
     * @param api          the ASM API version supported by this remapper. Must be one of the {@code
     *                     ASM}<i>x</i> values in {@link Opcodes}.
     * @param fieldVisitor the field visitor this remapper must delegate to.
     * @param remapper     the remapper to use to remap the types in the visited field.
     */
    protected FieldRemapper(final int api, final FieldVisitor fieldVisitor, final Remapper remapper) {
        super(api, fieldVisitor);
        this.remapper = remapper;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        AnnotationVisitor annotationVisitor =
                super.visitAnnotation(remapper.mapDesc(descriptor), visible);
        return annotationVisitor == null
                ? null
                : createAnnotationRemapper(descriptor, annotationVisitor);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(
            final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        AnnotationVisitor annotationVisitor =
                super.visitTypeAnnotation(typeRef, typePath, remapper.mapDesc(descriptor), visible);
        return annotationVisitor == null
                ? null
                : createAnnotationRemapper(descriptor, annotationVisitor);
    }

    /**
     * Constructs a new remapper for annotations. The default implementation of this method returns a
     * new {@link AnnotationRemapper}.
     *
     * @param annotationVisitor the AnnotationVisitor the remapper must delegate to.
     * @return the newly created remapper.
     * @deprecated use {@link #createAnnotationRemapper(String, AnnotationVisitor)} instead.
     */
    @Deprecated
    protected AnnotationVisitor createAnnotationRemapper(final AnnotationVisitor annotationVisitor) {
        return new AnnotationRemapper(api, /* descriptor = */ null, annotationVisitor, remapper);
    }

    /**
     * Constructs a new remapper for annotations. The default implementation of this method returns a
     * new {@link AnnotationRemapper}.
     *
     * @param descriptor        the descriptor of the visited annotation.
     * @param annotationVisitor the AnnotationVisitor the remapper must delegate to.
     * @return the newly created remapper.
     */
    protected AnnotationVisitor createAnnotationRemapper(
            final String descriptor, final AnnotationVisitor annotationVisitor) {
        return new AnnotationRemapper(api, descriptor, annotationVisitor, remapper)
                .orDeprecatedValue(createAnnotationRemapper(annotationVisitor));
    }
}
