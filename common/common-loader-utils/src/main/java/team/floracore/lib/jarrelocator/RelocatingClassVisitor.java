package team.floracore.lib.jarrelocator;

import team.floracore.lib.asm.ClassVisitor;
import team.floracore.lib.asm.ClassWriter;
import team.floracore.lib.asm.Opcodes;
import team.floracore.lib.asm.commons.ClassRemapper;

/**
 * A {@link ClassVisitor} that relocates types and names with a {@link RelocatingRemapper}.
 */
final class RelocatingClassVisitor extends ClassRemapper {
	private final String packageName;

	RelocatingClassVisitor(ClassWriter writer, RelocatingRemapper remapper, String name) {
		super(Opcodes.ASM9, writer, remapper);
		this.packageName = name.substring(0, name.lastIndexOf('/') + 1);
	}

	@Override
	public void visitSource(String source, String debug) {
		if (source == null) {
			super.visitSource(null, debug);
			return;
		}

		// visit source file name
		String name = this.packageName + source;
		String mappedName = super.remapper.map(name);
		String mappedFileName = mappedName.substring(mappedName.lastIndexOf('/') + 1);
		super.visitSource(mappedFileName, debug);
	}
}
