package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.ModuleVisitor;
import team.floracore.lib.asm.Type;

import java.util.List;

/**
 * A node that represents an opened package with its name and the module that can access it.
 *
 * @author Remi Forax
 */
public class ModuleOpenNode {

    /**
     * The internal name of the opened package (see {@link Type#getInternalName()}).
     */
    public String packaze;

    /**
     * The access flag of the opened package, valid values are among {@code ACC_SYNTHETIC} and {@code
     * ACC_MANDATED}.
     */
    public int access;

    /**
     * The fully qualified names (using dots) of the modules that can use deep reflection to the
     * classes of the open package, or {@literal null}.
     */
    public List<String> modules;

    /**
     * Constructs a new {@link ModuleOpenNode}.
     *
     * @param packaze the internal name of the opened package (see {@link
     *                Type#getInternalName()}).
     * @param access  the access flag of the opened package, valid values are among {@code
     *                ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     * @param modules the fully qualified names (using dots) of the modules that can use deep
     *                reflection to the classes of the open package, or {@literal null}.
     */
    public ModuleOpenNode(final String packaze, final int access, final List<String> modules) {
        this.packaze = packaze;
        this.access = access;
        this.modules = modules;
    }

    /**
     * Makes the given module visitor visit this opened package.
     *
     * @param moduleVisitor a module visitor.
     */
    public void accept(final ModuleVisitor moduleVisitor) {
        moduleVisitor.visitOpen(
                packaze, access, modules == null ? null : modules.toArray(new String[0]));
    }
}
