package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsWorldServer;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;

@WrappedBukkitClass(@VersionName("obc.CraftWorld"))
public interface ObcWorld extends WrappedBukkitObject {
	@WrappedMethod("getHandle")
	NmsWorldServer getHandle();
}
