package team.floracore.bukkit.util.wrappednms;

import com.google.common.collect.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagIntArray", maxVer = 17), @VersionName(value = "net.minecraft.nbt.NBTTagIntArray", minVer = 17)})
public interface NmsNBTTagIntArray extends NmsNBTTag {
	static NmsNBTTagIntArray newInstance(List<Integer> list) {
		return WrappedObject.getStatic(NmsNBTTagIntArray.class).staticNewInstance(list);
	}

	static NmsNBTTagIntArray newInstance(Integer... list) {
		return newInstance(Lists.newArrayList(list));
	}

	static NmsNBTTagIntArray newInstance(UUID uuid) {
		return newInstance((int) (uuid.getMostSignificantBits() >> 32), (int) uuid.getMostSignificantBits(), (int) (uuid.getLeastSignificantBits() >> 32), (int) uuid.getLeastSignificantBits());
	}

	@Override
	AbstractList<Object> getRaw();

	@WrappedConstructor
	NmsNBTTagIntArray staticNewInstance(List<Integer> list);

	default UUID asUUID() {
		return new UUID(((long) get(0)) << 32 + get(1), ((long) get(2)) << 32 + get(3));
	}

	default void set(int index, NmsNBTTagInt value) {
		getRaw().set(index, value.getRaw());
	}

	default void set(int index, int value) {
		set(index, NmsNBTTagInt.newInstance(value));
	}

	default NmsNBTTagInt getRaw(int index) {
		return WrappedObject.wrap(NmsNBTTagInt.class, getRaw().get(index));
	}

	default int get(int index) {
		return getRaw(index).getValue();
	}
}
