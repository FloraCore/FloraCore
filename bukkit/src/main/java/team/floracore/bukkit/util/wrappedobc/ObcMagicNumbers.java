package team.floracore.bukkit.util.wrappedobc;

import org.bukkit.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass(@VersionName("obc.util.CraftMagicNumbers"))
public interface ObcMagicNumbers extends WrappedBukkitObject {
	static NmsItem getItem(Material m) {
		return WrappedObject.getStatic(ObcMagicNumbers.class).staticGetItem(m);
	}

	static Material getMaterial(NmsItem item) {
		return WrappedObject.getStatic(ObcMagicNumbers.class).staticGetMaterial(item);
	}

	@WrappedMethod("getItem")
	NmsItem staticGetItem(Material m);

	@WrappedMethod("getMaterial")
	Material staticGetMaterial(NmsItem item);
}
