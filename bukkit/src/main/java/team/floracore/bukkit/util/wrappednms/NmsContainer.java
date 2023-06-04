package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.List;

@WrappedBukkitClass({@VersionName(value = "nms.Container", maxVer = 17),
		@VersionName(value = "net.minecraft.world.inventory.Container", minVer = 17)})
public interface NmsContainer extends WrappedBukkitObject {
	default NmsSlot getSlot(int index) {
		return WrappedObject.wrap(NmsSlot.class, getSlots().get(index));
	}

	default List<Object> getSlots() {
		if (BukkitWrapper.v17) {
			return getSlotsV17().getRaw();
		} else {
			return getSlotsV_17();
		}
	}

	@WrappedBukkitFieldAccessor({@VersionName(value = "slots", minVer = 17), @VersionName(value = "@1", minVer = 17)})
	NmsNonNullList getSlotsV17();

	@WrappedBukkitFieldAccessor({@VersionName(value = "slots", maxVer = 17), @VersionName(value = "@0", maxVer = 17)})
	List<Object> getSlotsV_17();

	default void setSlot(int index, NmsSlot slot) {
		getSlots().set(index, slot.getRaw());
	}

	@WrappedBukkitFieldAccessor({@VersionName("windowId"), @VersionName(minVer = 17, value = "j")})
	int getWindowId();

	@WrappedBukkitMethod({@VersionName(minVer = 17, value = "incrementStateId"), @VersionName(minVer = 18,
			value = "k")})
	int incrementStateIdV17();
}
