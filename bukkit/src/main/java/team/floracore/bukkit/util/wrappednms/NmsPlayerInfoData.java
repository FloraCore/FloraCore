package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappedmojang.WrappedGameProfile;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

/**
 * PacketPlayOutPlayerInfo的内部类
 *
 * @author xLikeWATCHDOG
 */
@WrappedBukkitClass(@VersionName(maxVer = 17, value = "nms.PacketPlayOutPlayerInfo$PlayerInfoData"))
public interface NmsPlayerInfoData extends WrappedBukkitObject {
	@WrappedBukkitFieldAccessor(@VersionName("@0"))
	WrappedGameProfile getGameProfile();

	@WrappedBukkitFieldAccessor(@VersionName("@0"))
	void setGameProfile(WrappedGameProfile gameProfile);

	@WrappedBukkitFieldAccessor(@VersionName("@0"))
	NmsIChatBaseComponent getIChatBaseComponent();

	@WrappedBukkitFieldAccessor(@VersionName("@0"))
	void setIChatBaseComponent(NmsIChatBaseComponent iChatBaseComponent);
}
