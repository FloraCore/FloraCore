package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.*;

/**
 * A {@link RecordComponentVisitor} that remaps types with a {@link Remapper}.
 *
 * @author Remi Forax
 */
public class RecordComponentRemapper extends RecordComponentVisitor {

    /**
     * The remapper used to remap the types in the visited field.
     */
    protected final Remapper remapper;

    /**
     * Constructs a new {@link RecordComponentRemapper}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the {@link
     * #RecordComponentRemapper(int, RecordComponentVisitor, Remapper)} version.
     *
     * @param recordComponentVisitor the record component visitor this remapper must delegate to.
     * @param remapper               the remapper to use to remap the types in the visited record component.
     */
    public RecordComponentRemapper(
            final RecordComponentVisitor recordComponentVisitor, final Remapper remapper) {
        this(/* latest api = */ Opcodes.ASM9, recordComponentVisitor, remapper);
    }

    /**
     * Constructs a new {@link RecordComponentRemapper}.
     *
     * @param api                    the ASM API version supported by this remapper. Must be one of {@link
     *                               Opcodes#ASM8} or {@link Opcodes#ASM9}.
     * @param recordComponentVisitor the record component visitor this remapper must delegate to.
     * @param remapper               the remapper to use to remap the types in the visited record component.
     */
    protected RecordComponentRemapper(
            final int api, final RecordComponentVisitor recordComponentVisitor, final Remapper remapper) {
        super(api, recordComponentVisitor);
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
     * @param descriptor        the descriptor sof the visited annotation.
     * @param annotationVisitor the AnnotationVisitor the remapper must delegate to.
     * @return the newly created remapper.
     */
    protected AnnotationVisitor createAnnotationRemapper(
            final String descriptor, final AnnotationVisitor annotationVisitor) {
        return new AnnotationRemapper(api, descriptor, annotationVisitor, remapper)
                .orDeprecatedValue(createAnnotationRemapper(annotationVisitor));
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
}
