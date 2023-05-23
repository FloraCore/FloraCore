package team.floracore.common.util.nothing;


import org.objectweb.asm.tree.*;
import team.floracore.common.util.*;

import java.lang.annotation.*;

/**
 * Generate bytecode injected manually
 * Injection must be a static method:
 * 1st arg is a {@link NothingMethod} meaning the injected method
 * 2nd arg is a {@link java.util.List}&lt;{@link AbstractInsnNode}> meaning the bytecodes of the method before being injected
 * 3rd arg is a {@link AbstractInsnNode} meaning location of injection, codes should be injected before it
 * Return void
 *
 * @see AsmUtil
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface ManualByteCode {
}
