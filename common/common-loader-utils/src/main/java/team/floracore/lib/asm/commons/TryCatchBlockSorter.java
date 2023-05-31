package team.floracore.lib.asm.commons;

import team.floracore.lib.asm.MethodVisitor;
import team.floracore.lib.asm.Opcodes;
import team.floracore.lib.asm.Type;
import team.floracore.lib.asm.tree.MethodNode;
import team.floracore.lib.asm.tree.TryCatchBlockNode;

import java.util.Collections;
import java.util.Comparator;

/**
 * A {@link MethodVisitor} adapter to sort the exception handlers. The handlers are sorted in a
 * method innermost-to-outermost. This allows the programmer to add handlers without worrying about
 * ordering them correctly with respect to existing, in-code handlers.
 *
 * <p>Behavior is only defined for properly-nested handlers. If any "try" blocks overlap (something
 * that isn't possible in Java code) then this may not do what you want. In fact, this adapter just
 * sorts by the length of the "try" block, taking advantage of the fact that a given try block must
 * be larger than any block it contains).
 *
 * @author Adrian Sampson
 */
public class TryCatchBlockSorter extends MethodNode {

    /**
     * Constructs a new {@link TryCatchBlockSorter}.
     *
     * @param methodVisitor the method visitor to which this visitor must delegate method calls. May
     *                      be {@literal null}.
     * @param access        the method's access flags (see {@link Opcodes}). This parameter also indicates if
     *                      the method is synthetic and/or deprecated.
     * @param name          the method's name.
     * @param descriptor    the method's descriptor (see {@link Type}).
     * @param signature     the method's signature. May be {@literal null} if the method parameters,
     *                      return type and exceptions do not use generic types.
     * @param exceptions    the internal names of the method's exception classes (see {@link
     *                      Type#getInternalName()}). May be {@literal null}.
     */
    public TryCatchBlockSorter(
            final MethodVisitor methodVisitor,
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions) {
        this(
                /* latest api = */ Opcodes.ASM9,
                                   methodVisitor,
                                   access,
                                   name,
                                   descriptor,
                                   signature,
                                   exceptions);
        if (getClass() != TryCatchBlockSorter.class) {
            throw new IllegalStateException();
        }
    }

    protected TryCatchBlockSorter(
            final int api,
            final MethodVisitor methodVisitor,
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
        this.mv = methodVisitor;
    }

    @Override
    public void visitEnd() {
        // Sort the TryCatchBlockNode elements by the length of their "try" block.
        Collections.sort(
                tryCatchBlocks,
                new Comparator<TryCatchBlockNode>() {

                    @Override
                    public int compare(
                            final TryCatchBlockNode tryCatchBlockNode1,
                            final TryCatchBlockNode tryCatchBlockNode2) {
                        return blockLength(tryCatchBlockNode1) - blockLength(tryCatchBlockNode2);
                    }

                    private int blockLength(final TryCatchBlockNode tryCatchBlockNode) {
                        int startIndex = instructions.indexOf(tryCatchBlockNode.start);
                        int endIndex = instructions.indexOf(tryCatchBlockNode.end);
                        return endIndex - startIndex;
                    }
                });
        // Update the 'target' of each try catch block annotation.
        for (int i = 0; i < tryCatchBlocks.size(); ++i) {
            tryCatchBlocks.get(i).updateIndex(i);
        }
        if (mv != null) {
            accept(mv);
        }
    }
}
