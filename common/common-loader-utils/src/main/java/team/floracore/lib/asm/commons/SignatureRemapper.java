
package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.Opcodes;
import team.floracore.lib.asm.signature.SignatureVisitor;

import java.util.ArrayList;

/**
 * A {@link SignatureVisitor} that remaps types with a {@link Remapper}.
 *
 * @author Eugene Kuleshov
 */
public class SignatureRemapper extends SignatureVisitor {

    private final SignatureVisitor signatureVisitor;

    private final Remapper remapper;

    private final ArrayList<String> classNames = new ArrayList<>();

    /**
     * Constructs a new {@link SignatureRemapper}. <i>Subclasses must not use this constructor</i>.
     * Instead, they must use the {@link #SignatureRemapper(int, SignatureVisitor, Remapper)} version.
     *
     * @param signatureVisitor the signature visitor this remapper must delegate to.
     * @param remapper         the remapper to use to remap the types in the visited signature.
     */
    public SignatureRemapper(final SignatureVisitor signatureVisitor, final Remapper remapper) {
        this(/* latest api = */ Opcodes.ASM9, signatureVisitor, remapper);
    }

    /**
     * Constructs a new {@link SignatureRemapper}.
     *
     * @param api              the ASM API version supported by this remapper. Must be one of the {@code
     *                         ASM}<i>x</i> values in {@link Opcodes}.
     * @param signatureVisitor the signature visitor this remapper must delegate to.
     * @param remapper         the remapper to use to remap the types in the visited signature.
     */
    protected SignatureRemapper(
            final int api, final SignatureVisitor signatureVisitor, final Remapper remapper) {
        super(api);
        this.signatureVisitor = signatureVisitor;
        this.remapper = remapper;
    }

    @Override
    public void visitClassType(final String name) {
        classNames.add(name);
        signatureVisitor.visitClassType(remapper.mapType(name));
    }

    @Override
    public void visitInnerClassType(final String name) {
        String outerClassName = classNames.remove(classNames.size() - 1);
        String className = outerClassName + '$' + name;
        classNames.add(className);
        String remappedOuter = remapper.mapType(outerClassName) + '$';
        String remappedName = remapper.mapType(className);
        int index =
                remappedName.startsWith(remappedOuter)
                        ? remappedOuter.length()
                        : remappedName.lastIndexOf('$') + 1;
        signatureVisitor.visitInnerClassType(remappedName.substring(index));
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        signatureVisitor.visitFormalTypeParameter(name);
    }

    @Override
    public void visitTypeVariable(final String name) {
        signatureVisitor.visitTypeVariable(name);
    }

    @Override
    public SignatureVisitor visitArrayType() {
        signatureVisitor.visitArrayType();
        return this;
    }

    @Override
    public void visitBaseType(final char descriptor) {
        signatureVisitor.visitBaseType(descriptor);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        signatureVisitor.visitClassBound();
        return this;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        signatureVisitor.visitExceptionType();
        return this;
    }

    @Override
    public SignatureVisitor visitInterface() {
        signatureVisitor.visitInterface();
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        signatureVisitor.visitInterfaceBound();
        return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        signatureVisitor.visitParameterType();
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        signatureVisitor.visitReturnType();
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        signatureVisitor.visitSuperclass();
        return this;
    }

    @Override
    public void visitTypeArgument() {
        signatureVisitor.visitTypeArgument();
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        signatureVisitor.visitTypeArgument(wildcard);
        return this;
    }

    @Override
    public void visitEnd() {
        signatureVisitor.visitEnd();
        classNames.remove(classNames.size() - 1);
    }
}
