package team.floracore.bukkit.util.wrappednms;

import team.floracore.common.util.wrapper.WrappedArray;
import team.floracore.common.util.wrapper.WrappedArrayClass;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedArrayClass(NmsItemStack.class)
public interface NmsItemStackArray extends WrappedArray<NmsItemStack> {
    static NmsItemStackArray newInstance(int length) {
        return (NmsItemStackArray) WrappedObject.getStatic(NmsItemStackArray.class).staticNewInstance(length);
    }
}
