package team.floracore.bukkit.util.wrappednms;

import org.bukkit.inventory.ItemStack;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.ArrayList;
import java.util.Random;

@WrappedBukkitClass({@VersionName(value = "nms.EntityLiving", maxVer = 17),
		@VersionName(value = "net.minecraft.world.entity.EntityLiving", minVer = 17)})
public interface NmsEntityLiving extends NmsEntity {
	static NmsDataWatcherObject getUsingItemDataWatcherObjectV_13() {
		return WrappedObject.getStatic(NmsEntityLiving.class).staticGetUsingItemDataWatcherObjectV_13();
	}

	@WrappedBukkitFieldAccessor(@VersionName(value = "at", maxVer = 13))
	NmsDataWatcherObject staticGetUsingItemDataWatcherObjectV_13();

	@WrappedBukkitFieldAccessor(@VersionName("forceDrops"))
	boolean isForceDrops();

	@WrappedBukkitFieldAccessor(@VersionName("drops"))
	ArrayList<ItemStack> getDrops();

	@WrappedBukkitMethod({@VersionName(value = "getRandom", maxVer = 19),
			@VersionName(value = "@0", minVer = 17, maxVer = 19)})
	Random getRandomV_19();

	@WrappedBukkitMethod(@VersionName(value = {"getRandom", "@0"}, minVer = 19))
	NmsRandomSourceV19 getRandomV19();
}
