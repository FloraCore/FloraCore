package team.floracore.bukkit.util.wrappednms;

import org.bukkit.block.data.BlockData;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitConstructor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayOutBlockChange",
		maxVer = 17), @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayOutBlockChange",
		minVer = 17)})
public interface NmsPacketPlayOutBlockChange extends NmsPacket {
	static NmsPacketPlayOutBlockChange newInstance() {
		return WrappedObject.getStatic(NmsPacketPlayOutBlockChange.class).staticNewInstance();
	}

	@WrappedBukkitConstructor
	NmsPacketPlayOutBlockChange staticNewInstance();

	@WrappedBukkitFieldAccessor(@VersionName("a"))
	void setBlockPosition(NmsBlockPosition blockPosition);

	@WrappedBukkitFieldAccessor(@VersionName("block"))
	void setBlockData(BlockData blockData);
}
