package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.Attribute;
import team.floracore.lib.asm.ByteVector;
import team.floracore.lib.asm.ClassReader;
import team.floracore.lib.asm.ClassVisitor;
import team.floracore.lib.asm.ClassWriter;
import team.floracore.lib.asm.Label;

/**
 * A ModuleTarget attribute. This attribute is specific to the OpenJDK and may change in the future.
 *
 * @author Remi Forax
 */
public final class ModuleTargetAttribute extends Attribute {

    /**
     * The name of the platform on which the module can run.
     */
    public String platform;

    /**
     * Constructs a new {@link ModuleTargetAttribute}.
     *
     * @param platform the name of the platform on which the module can run.
     */
    public ModuleTargetAttribute(final String platform) {
        super("ModuleTarget");
        this.platform = platform;
    }

    /**
     * Constructs an empty {@link ModuleTargetAttribute}. This object can be passed as a prototype to
     * the {@link ClassReader#accept(ClassVisitor, Attribute[], int)} method.
     */
    public ModuleTargetAttribute() {
        this(null);
    }

    @Override
    protected Attribute read(
            final ClassReader classReader,
            final int offset,
            final int length,
            final char[] charBuffer,
            final int codeOffset,
            final Label[] labels) {
        return new ModuleTargetAttribute(classReader.readUTF8(offset, charBuffer));
    }

    @Override
    protected ByteVector write(
            final ClassWriter classWriter,
            final byte[] code,
            final int codeLength,
            final int maxStack,
            final int maxLocals) {
        ByteVector byteVector = new ByteVector();
        byteVector.putShort(platform == null ? 0 : classWriter.newUTF8(platform));
        return byteVector;
    }
}
