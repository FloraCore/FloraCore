package team.floracore.bukkit.util.wrappednms;


import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.RegistryID", maxVer = 17), @VersionName(value = "net.minecraft.util.RegistryID", minVer = 17)})
public interface NmsRegistryID extends WrappedBukkitObject {
	@WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "b"))
	Object[] getBV_13();

	@WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "c"))
	int[] getCV_13();

	@WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "d"))
	Object[] getDV_13();

	@WrappedBukkitMethod(@VersionName(value = "getId", maxVer = 13))
	int getIdV_13(Object o);

	@WrappedBukkitMethod(@VersionName(value = "e", minVer = 13))
	int eV13(int i);

	@WrappedBukkitMethod(@VersionName(value = "d", minVer = 13))
	int dV13(Object o);
}
