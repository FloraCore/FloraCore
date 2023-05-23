package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.MinecraftServer", maxVer = 17), @VersionName(value = "net.minecraft.server.MinecraftServer", minVer = 17)})
public interface NmsMinecraftServer extends NmsICommandListener {
	static NmsMinecraftServer getServer() {
		return WrappedObject.getStatic(NmsMinecraftServer.class).staticGetServer();
	}

	@WrappedMethod("getServer")
	NmsMinecraftServer staticGetServer();

	@WrappedBukkitMethod(@VersionName(minVer = 13, value = {"getCraftingManager", "@0"}))
	NmsCraftingManager getCraftingManagerV13();
}
