package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.Attribute;
import team.floracore.lib.asm.ByteVector;
import team.floracore.lib.asm.ClassReader;
import team.floracore.lib.asm.ClassVisitor;
import team.floracore.lib.asm.ClassWriter;
import team.floracore.lib.asm.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * A ModuleHashes attribute. This attribute is specific to the OpenJDK and may change in the future.
 *
 * @author Remi Forax
 */
public final class ModuleHashesAttribute extends Attribute {

	/**
	 * The name of the hashing algorithm.
	 */
	public String algorithm;

	/**
	 * A list of module names.
	 */
	public List<String> modules;

	/**
	 * The hash of the modules in {@link #modules}. The two lists must have the same size.
	 */
	public List<byte[]> hashes;

	/**
	 * Constructs a new {@link ModuleHashesAttribute}.
	 *
	 * @param algorithm the name of the hashing algorithm.
	 * @param modules   a list of module names.
	 * @param hashes    the hash of the modules in 'modules'. The two lists must have the same size.
	 */
	public ModuleHashesAttribute(
			final String algorithm, final List<String> modules, final List<byte[]> hashes) {
		super("ModuleHashes");
		this.algorithm = algorithm;
		this.modules = modules;
		this.hashes = hashes;
	}

	/**
	 * Constructs an empty {@link ModuleHashesAttribute}. This object can be passed as a prototype to
	 * the {@link ClassReader#accept(ClassVisitor, Attribute[], int)} method.
	 */
	public ModuleHashesAttribute() {
		this(null, null, null);
	}

	@Override
	protected Attribute read(
			final ClassReader classReader,
			final int offset,
			final int length,
			final char[] charBuffer,
			final int codeAttributeOffset,
			final Label[] labels) {
		int currentOffset = offset;

		String hashAlgorithm = classReader.readUTF8(currentOffset, charBuffer);
		currentOffset += 2;

		int numModules = classReader.readUnsignedShort(currentOffset);
		currentOffset += 2;

		ArrayList<String> moduleList = new ArrayList<>(numModules);
		ArrayList<byte[]> hashList = new ArrayList<>(numModules);

		for (int i = 0; i < numModules; ++i) {
			String module = classReader.readModule(currentOffset, charBuffer);
			currentOffset += 2;
			moduleList.add(module);

			int hashLength = classReader.readUnsignedShort(currentOffset);
			currentOffset += 2;
			byte[] hash = new byte[hashLength];
			for (int j = 0; j < hashLength; ++j) {
				hash[j] = (byte) classReader.readByte(currentOffset);
				currentOffset += 1;
			}
			hashList.add(hash);
		}
		return new ModuleHashesAttribute(hashAlgorithm, moduleList, hashList);
	}

	@Override
	protected ByteVector write(
			final ClassWriter classWriter,
			final byte[] code,
			final int codeLength,
			final int maxStack,
			final int maxLocals) {
		ByteVector byteVector = new ByteVector();
		byteVector.putShort(classWriter.newUTF8(algorithm));
		if (modules == null) {
			byteVector.putShort(0);
		} else {
			int numModules = modules.size();
			byteVector.putShort(numModules);
			for (int i = 0; i < numModules; ++i) {
				String module = modules.get(i);
				byte[] hash = hashes.get(i);
				byteVector
						.putShort(classWriter.newModule(module))
						.putShort(hash.length)
						.putByteArray(hash, 0, hash.length);
			}
		}
		return byteVector;
	}
}
