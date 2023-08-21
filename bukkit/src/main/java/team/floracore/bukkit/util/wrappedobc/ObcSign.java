package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsTileEntitySign;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass(@VersionName("obc.block.CraftSign"))
public interface ObcSign extends WrappedBukkitObject {
	@WrappedBukkitFieldAccessor(@VersionName(minVer = 8, maxVer = 12, value = "sign"))
	NmsTileEntitySign getTileEntitySign();
}
