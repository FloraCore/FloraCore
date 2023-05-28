package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.*;

/**
 * A {@link ModuleVisitor} that remaps types with a {@link Remapper}.
 *
 * @author Remi Forax
 */
public class ModuleRemapper extends ModuleVisitor {

    /**
     * The remapper used to remap the types in the visited module.
     */
    protected final Remapper remapper;

    /**
     * Constructs a new {@link ModuleRemapper}. <i>Subclasses must not use this constructor</i>.
     * Instead, they must use the {@link #ModuleRemapper(int, ModuleVisitor, Remapper)} version.
     *
     * @param moduleVisitor the module visitor this remapper must delegate to.
     * @param remapper      the remapper to use to remap the types in the visited module.
     */
    public ModuleRemapper(final ModuleVisitor moduleVisitor, final Remapper remapper) {
        this(/* latest api = */ Opcodes.ASM9, moduleVisitor, remapper);
    }

    /**
     * Constructs a new {@link ModuleRemapper}.
     *
     * @param api           the ASM API version supported by this remapper. Must be one of the {@code
     *                      ASM}<i>x</i> values in {@link Opcodes}.
     * @param moduleVisitor the module visitor this remapper must delegate to.
     * @param remapper      the remapper to use to remap the types in the visited module.
     */
    protected ModuleRemapper(
            final int api, final ModuleVisitor moduleVisitor, final Remapper remapper) {
        super(api, moduleVisitor);
        this.remapper = remapper;
    }

    @Override
    public void visitMainClass(final String mainClass) {
        super.visitMainClass(remapper.mapType(mainClass));
    }

    @Override
    public void visitPackage(final String packaze) {
        super.visitPackage(remapper.mapPackageName(packaze));
    }

    @Override
    public void visitRequire(final String module, final int access, final String version) {
        super.visitRequire(remapper.mapModuleName(module), access, version);
    }

    @Override
    public void visitExport(final String packaze, final int access, final String... modules) {
        String[] remappedModules = null;
        if (modules != null) {
            remappedModules = new String[modules.length];
            for (int i = 0; i < modules.length; ++i) {
                remappedModules[i] = remapper.mapModuleName(modules[i]);
            }
        }
        super.visitExport(remapper.mapPackageName(packaze), access, remappedModules);
    }

    @Override
    public void visitOpen(final String packaze, final int access, final String... modules) {
        String[] remappedModules = null;
        if (modules != null) {
            remappedModules = new String[modules.length];
            for (int i = 0; i < modules.length; ++i) {
                remappedModules[i] = remapper.mapModuleName(modules[i]);
            }
        }
        super.visitOpen(remapper.mapPackageName(packaze), access, remappedModules);
    }

    @Override
    public void visitUse(final String service) {
        super.visitUse(remapper.mapType(service));
    }

    @Override
    public void visitProvide(final String service, final String... providers) {
        String[] remappedProviders = new String[providers.length];
        for (int i = 0; i < providers.length; ++i) {
            remappedProviders[i] = remapper.mapType(providers[i]);
        }
        super.visitProvide(remapper.mapType(service), remappedProviders);
    }
}
