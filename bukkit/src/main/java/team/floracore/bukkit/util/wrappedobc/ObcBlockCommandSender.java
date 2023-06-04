package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsICommandListener;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;

@WrappedBukkitClass(@VersionName("obc.command.CraftBlockCommandSender"))
public interface ObcBlockCommandSender extends WrappedBukkitObject {
	@WrappedMethod(value = "getTileEntity")
	NmsICommandListener getTileEntity();
}
