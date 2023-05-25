package team.floracore.lib.asm.util;

import team.floracore.lib.asm.*;

/**
 * An {@link AnnotationVisitor} that checks that its methods are properly used.
 *
 * @author Eric Bruneton
 */
public class CheckAnnotationAdapter extends AnnotationVisitor {

    /**
     * Whether the values of the visited annotation are named. AnnotationVisitor instances used for
     * annotation default and annotation arrays use unnamed values.
     */
    private final boolean useNamedValue;

    /**
     * Whether the {@link #visitEnd} method has been called.
     */
    private boolean visitEndCalled;

    public CheckAnnotationAdapter(final AnnotationVisitor annotationVisitor) {
        this(annotationVisitor, true);
    }

    CheckAnnotationAdapter(final AnnotationVisitor annotationVisitor, final boolean useNamedValues) {
        super(/* latest api = */ Opcodes.ASM9, annotationVisitor);
        this.useNamedValue = useNamedValues;
    }

    @Override
    public void visit(final String name, final Object value) {
        checkVisitEndNotCalled();
        checkName(name);
        if (!(value instanceof Byte
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long
                || value instanceof Float
                || value instanceof Double
                || value instanceof String
                || value instanceof Type
                || value instanceof byte[]
                || value instanceof boolean[]
                || value instanceof char[]
                || value instanceof short[]
                || value instanceof int[]
                || value instanceof long[]
                || value instanceof float[]
                || value instanceof double[])) {
            throw new IllegalArgumentException("Invalid annotation value");
        }
        if (value instanceof Type && ((Type) value).getSort() == Type.METHOD) {
            throw new IllegalArgumentException("Invalid annotation value");
        }
        super.visit(name, value);
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        checkVisitEndNotCalled();
        checkName(name);
        // Annotations can only appear in V1_5 or more classes.
        CheckMethodAdapter.checkDescriptor(Opcodes.V1_5, descriptor, false);
        if (value == null) {
            throw new IllegalArgumentException("Invalid enum value");
        }
        super.visitEnum(name, descriptor, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        checkVisitEndNotCalled();
        checkName(name);
        // Annotations can only appear in V1_5 or more classes.
        CheckMethodAdapter.checkDescriptor(Opcodes.V1_5, descriptor, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(name, descriptor));
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        checkVisitEndNotCalled();
        checkName(name);
        return new CheckAnnotationAdapter(super.visitArray(name), false);
    }

    @Override
    public void visitEnd() {
        checkVisitEndNotCalled();
        visitEndCalled = true;
        super.visitEnd();
    }

    private void checkVisitEndNotCalled() {
        if (visitEndCalled) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
        }
    }

    private void checkName(final String name) {
        if (useNamedValue && name == null) {
            throw new IllegalArgumentException("Annotation value name must not be null");
        }
    }
}
