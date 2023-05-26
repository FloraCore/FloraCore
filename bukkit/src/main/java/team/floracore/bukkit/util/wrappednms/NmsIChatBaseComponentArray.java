package team.floracore.bukkit.util.wrappednms;

import team.floracore.common.util.wrapper.*;

@WrappedArrayClass(NmsIChatBaseComponent.class)
public interface NmsIChatBaseComponentArray extends WrappedArray<NmsIChatBaseComponent> {
    static NmsIChatBaseComponentArray newInstance(int length) {
        return (NmsIChatBaseComponentArray) WrappedObject.getStatic(NmsIChatBaseComponentArray.class)
                .staticNewInstance(length);
    }
}
