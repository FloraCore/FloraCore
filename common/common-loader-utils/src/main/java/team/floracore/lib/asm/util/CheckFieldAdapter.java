package team.floracore.lib.asm.util;

import team.floracore.lib.asm.AnnotationVisitor;
import team.floracore.lib.asm.Attribute;
import team.floracore.lib.asm.FieldVisitor;
import team.floracore.lib.asm.Opcodes;
import team.floracore.lib.asm.TypePath;
import team.floracore.lib.asm.TypeReference;

/**
 * A {@link FieldVisitor} that checks that its methods are properly used.
 *
 * @author Eric Bruneton
 */
public class CheckFieldAdapter extends FieldVisitor {

	/**
	 * Whether the {@link #visitEnd} method has been called.
	 */
	private boolean visitEndCalled;

	/**
	 * Constructs a new {@link CheckFieldAdapter}. <i>Subclasses must not use this constructor</i>.
	 * Instead, they must use the {@link #CheckFieldAdapter(int, FieldVisitor)} version.
	 *
	 * @param fieldVisitor the field visitor to which this adapter must delegate calls.
	 * @throws IllegalStateException If a subclass calls this constructor.
	 */
	public CheckFieldAdapter(final FieldVisitor fieldVisitor) {
		this(/* latest api = */ Opcodes.ASM9, fieldVisitor);
		if (getClass() != CheckFieldAdapter.class) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Constructs a new {@link CheckFieldAdapter}.
	 *
	 * @param api          the ASM API version implemented by this visitor. Must be one of the {@code
	 *                     ASM}<i>x</i> values in {@link Opcodes}.
	 * @param fieldVisitor the field visitor to which this adapter must delegate calls.
	 */
	protected CheckFieldAdapter(final int api, final FieldVisitor fieldVisitor) {
		super(api, fieldVisitor);
	}

	@Override
	public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
		checkVisitEndNotCalled();
		// Annotations can only appear in V1_5 or more classes.
		CheckMethodAdapter.checkDescriptor(Opcodes.V1_5, descriptor, false);
		return new CheckAnnotationAdapter(super.visitAnnotation(descriptor, visible));
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(
			final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
		checkVisitEndNotCalled();
		int sort = new TypeReference(typeRef).getSort();
		if (sort != TypeReference.FIELD) {
			throw new IllegalArgumentException(
					"Invalid type reference sort 0x" + Integer.toHexString(sort));
		}
		CheckClassAdapter.checkTypeRef(typeRef);
		CheckMethodAdapter.checkDescriptor(Opcodes.V1_5, descriptor, false);
		return new CheckAnnotationAdapter(
				super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
	}

	@Override
	public void visitAttribute(final Attribute attribute) {
		checkVisitEndNotCalled();
		if (attribute == null) {
			throw new IllegalArgumentException("Invalid attribute (must not be null)");
		}
		super.visitAttribute(attribute);
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
}
