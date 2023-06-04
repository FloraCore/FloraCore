package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;

@WrappedBukkitClass({@VersionName(value = "nms.ContainerPlayer", maxVer = 17),
		@VersionName(value = "net.minecraft.world.inventory.ContainerPlayer", minVer = 17)})
public interface NmsContainerPlayer extends NmsContainer {
}
