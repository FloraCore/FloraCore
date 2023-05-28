package team.floracore.lib.asm.tree;

import team.floracore.lib.asm.*;

import java.util.*;

/**
 * A node that represents a service and its implementation provided by the current module.
 *
 * @author Remi Forax
 */
public class ModuleProvideNode {

    /**
     * The internal name of the service (see {@link Type#getInternalName()}).
     */
    public String service;

    /**
     * The internal names of the implementations of the service (there is at least one provider). See
     * {@link Type#getInternalName()}.
     */
    public List<String> providers;

    /**
     * Constructs a new {@link ModuleProvideNode}.
     *
     * @param service   the internal name of the service.
     * @param providers the internal names of the implementations of the service (there is at least
     *                  one provider). See {@link Type#getInternalName()}.
     */
    public ModuleProvideNode(final String service, final List<String> providers) {
        this.service = service;
        this.providers = providers;
    }

    /**
     * Makes the given module visitor visit this require declaration.
     *
     * @param moduleVisitor a module visitor.
     */
    public void accept(final ModuleVisitor moduleVisitor) {
        moduleVisitor.visitProvide(service, providers.toArray(new String[0]));
    }
}
