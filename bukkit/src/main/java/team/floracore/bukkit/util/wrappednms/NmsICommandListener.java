package team.floracore.bukkit.util.wrappednms;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappedobc.ObcBlockCommandSender;
import team.floracore.bukkit.util.wrappedobc.ObcEntity;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.UUID;

@WrappedBukkitClass({@VersionName(value = "nms.ICommandListener", maxVer = 17),
		@VersionName(value = "net.minecraft.commands.ICommandListener", minVer = 17)})
public interface NmsICommandListener extends WrappedBukkitObject {
	static NmsICommandListener fromBukkit(CommandSender sender) {
		if (sender instanceof BlockCommandSender) {
			return WrappedObject.wrap(ObcBlockCommandSender.class, sender).getTileEntity();
		} else if (sender instanceof ConsoleCommandSender) {
			return NmsMinecraftServer.getServer();
		} else if (sender instanceof Entity) {
			return WrappedObject.wrap(ObcEntity.class, sender).getHandle();
		} else if (sender instanceof GeneraldutyCommandSender) {
			return ((GeneraldutyCommandSender) sender).getNms();
		} else {
			throw new IllegalArgumentException("unsupported type " + sender.getClass().getName() + " of " +
					"CommandSender");
		}
	}

	default void sendMessage(NmsIChatBaseComponent msg) {
		if (BukkitWrapper.version < 16) {
			sendMessageV_16(msg);
		} else if (BukkitWrapper.version < 19) {
			sendMessageV16_19(msg, new UUID(0L, 0L));
		} else {
			sendMessageV19(msg);
		}
	}

	@WrappedBukkitMethod(@VersionName(maxVer = 16, value = "sendMessage"))
	void sendMessageV_16(NmsIChatBaseComponent msg);

	@WrappedBukkitMethod({@VersionName(minVer = 16, maxVer = 19, value = "sendMessage"),
			@VersionName(value = "a", minVer = 18, maxVer = 19)})
	void sendMessageV16_19(NmsIChatBaseComponent msg, UUID sender);

	@WrappedBukkitMethod(@VersionName(value = "a", minVer = 19))
	void sendMessageV19(NmsIChatBaseComponent msg);

	interface GeneraldutyCommandSender extends CommandSender {
		NmsICommandListener getNms();
	}
}
