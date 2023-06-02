package team.floracore.bukkit.util.wrappednms;

import com.google.common.collect.Lists;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.common.util.wrapper.WrappedConstructor;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.AbstractList;
import java.util.List;
import java.util.UUID;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagIntArray",
        maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagIntArray", minVer = 17)})
public interface NmsNBTTagIntArray extends NmsNBTTag {
    static NmsNBTTagIntArray newInstance(UUID uuid) {
        return newInstance((int) (uuid.getMostSignificantBits() >> 32),
                (int) uuid.getMostSignificantBits(),
                (int) (uuid.getLeastSignificantBits() >> 32),
                (int) uuid.getLeastSignificantBits());
    }

    static NmsNBTTagIntArray newInstance(Integer... list) {
        return newInstance(Lists.newArrayList(list));
    }

    static NmsNBTTagIntArray newInstance(List<Integer> list) {
        return WrappedObject.getStatic(NmsNBTTagIntArray.class).staticNewInstance(list);
    }

    @WrappedConstructor
    NmsNBTTagIntArray staticNewInstance(List<Integer> list);

    default UUID asUUID() {
        return new UUID(((long) get(0)) << 32 + get(1), ((long) get(2)) << 32 + get(3));
    }

    default int get(int index) {
        return getRaw(index).getValue();
    }

    default NmsNBTTagInt getRaw(int index) {
        return WrappedObject.wrap(NmsNBTTagInt.class, getRaw().get(index));
    }

    @Override
    AbstractList<Object> getRaw();

    default void set(int index, int value) {
        set(index, NmsNBTTagInt.newInstance(value));
    }

    default void set(int index, NmsNBTTagInt value) {
        getRaw().set(index, value.getRaw());
    }
}
