package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass(@VersionName(value = "net.minecraft.util.RandomSource", minVer = 19))
public interface NmsRandomSourceV19 extends WrappedBukkitObject {
	@WrappedBukkitMethod(@VersionName("@0"))
	float nextFloat();
}
