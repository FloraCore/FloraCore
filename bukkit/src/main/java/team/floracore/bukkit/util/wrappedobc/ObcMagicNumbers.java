package team.floracore.bukkit.util.wrappedobc;

import org.bukkit.Material;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsItem;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;
import team.floracore.common.util.wrapper.WrappedObject;

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
