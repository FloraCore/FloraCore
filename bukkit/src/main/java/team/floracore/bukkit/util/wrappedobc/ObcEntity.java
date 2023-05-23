package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass(@VersionName("obc.entity.CraftEntity"))
public interface ObcEntity extends WrappedBukkitObject {
	@WrappedMethod("getHandle")
	NmsEntity getHandle();
}
