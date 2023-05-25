package team.floracore.common.util.wrapper;

import team.floracore.common.util.*;
import team.floracore.lib.asm.*;
import team.floracore.lib.asm.tree.*;

import java.lang.reflect.*;

@WrappedArrayClass(WrappedObject.class)
public interface WrappedArray<T extends WrappedObject> extends WrappedObject {
    default void set(int index, T element) {
        getRaw()[index] = element.getRaw();
    }

    @Override
    Object[] getRaw();

    @Override
    default void apply(ClassNode cn, Class<? extends WrappedObject> wrapper) {
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "getElementWrapper", AsmUtil.getDesc(new Class[0], Class.class), null, new String[0]);
        mn.instructions.add(AsmUtil.ldcNode(getElementWrapper(wrapper)));
        mn.instructions.add(AsmUtil.returnNode(Class.class));
        cn.methods.add(mn);
    }

    default Class<T> getElementWrapper(Class<? extends WrappedObject> wrapper) {
        WrappedArrayClass ann = wrapper.getDeclaredAnnotation(WrappedArrayClass.class);
        if (ann != null) {
            return TypeUtil.cast(ann.value());
        }
        return null;
    }

    @Override
    default Class<?> getAnnotationClass(Class<? extends WrappedObject> wrapper) {
        Class<T> ew = getElementWrapper(wrapper);
        if (ew != null) {
            return Array.newInstance(WrappedObject.getRawClass(ew), 0).getClass();
        }
        return null;
    }

    default int length() {
        return getRaw().length;
    }

    default WrappedArray<T> staticNewInstance(int length) {
        return TypeUtil.cast(WrappedObject.wrap(getWrapper(), Array.newInstance(WrappedObject.getRawClass(getElementWrapper()), length)));
    }

    Class<T> getElementWrapper();

    default T get(int index) {
        return WrappedObject.wrap(getElementWrapper(), getRaw()[index]);
    }
}
