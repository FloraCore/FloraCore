package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.ClassVisitor;
import team.floracore.lib.asm.MethodVisitor;
import team.floracore.lib.asm.Opcodes;

/**
 * A {@link ClassVisitor} that merges &lt;clinit&gt; methods into a single one. All the existing
 * &lt;clinit&gt; methods are renamed, and a new one is created, which calls all the renamed
 * methods.
 *
 * @author Eric Bruneton
 */
public class StaticInitMerger extends ClassVisitor {

	/**
	 * The prefix to use to rename the existing &lt;clinit&gt; methods.
	 */
	private final String renamedClinitMethodPrefix;
	/**
	 * The internal name of the visited class.
	 */
	private String owner;
	/**
	 * The number of &lt;clinit&gt; methods visited so far.
	 */
	private int numClinitMethods;

	/**
	 * The MethodVisitor for the merged &lt;clinit&gt; method.
	 */
	private MethodVisitor mergedClinitVisitor;

	/**
	 * Constructs a new {@link StaticInitMerger}. <i>Subclasses must not use this constructor</i>.
	 * Instead, they must use the {@link #StaticInitMerger(int, String, ClassVisitor)} version.
	 *
	 * @param prefix       the prefix to use to rename the existing &lt;clinit&gt; methods.
	 * @param classVisitor the class visitor to which this visitor must delegate method calls. May be
	 *                     null.
	 */
	public StaticInitMerger(final String prefix, final ClassVisitor classVisitor) {
		this(/* latest api = */ Opcodes.ASM9, prefix, classVisitor);
	}

	/**
	 * Constructs a new {@link StaticInitMerger}.
	 *
	 * @param api          the ASM API version implemented by this visitor. Must be one of the {@code
	 *                     ASM}<i>x</i> values in {@link Opcodes}.
	 * @param prefix       the prefix to use to rename the existing &lt;clinit&gt; methods.
	 * @param classVisitor the class visitor to which this visitor must delegate method calls. May be
	 *                     null.
	 */
	protected StaticInitMerger(final int api, final String prefix, final ClassVisitor classVisitor) {
		super(api, classVisitor);
		this.renamedClinitMethodPrefix = prefix;
	}

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.owner = name;
	}

	@Override
	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String descriptor,
			final String signature,
			final String[] exceptions) {
		MethodVisitor methodVisitor;
		if ("<clinit>".equals(name)) {
			int newAccess = Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC;
			String newName = renamedClinitMethodPrefix + numClinitMethods++;
			methodVisitor = super.visitMethod(newAccess, newName, descriptor, signature, exceptions);

			if (mergedClinitVisitor == null) {
				mergedClinitVisitor = super.visitMethod(newAccess, name, descriptor, null, null);
			}
			mergedClinitVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, owner, newName, descriptor, false);
		} else {
			methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
		}
		return methodVisitor;
	}

	@Override
	public void visitEnd() {
		if (mergedClinitVisitor != null) {
			mergedClinitVisitor.visitInsn(Opcodes.RETURN);
			mergedClinitVisitor.visitMaxs(0, 0);
		}
		super.visitEnd();
	}
}
