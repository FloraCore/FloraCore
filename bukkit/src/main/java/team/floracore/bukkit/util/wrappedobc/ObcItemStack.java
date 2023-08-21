package team.floracore.bukkit.util.wrappedobc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsItemStack;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedFieldAccessor;
import team.floracore.common.util.wrapper.WrappedMethod;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.Objects;

@WrappedBukkitClass(@VersionName("obc.inventory.CraftItemStack"))
public interface ObcItemStack extends WrappedBukkitObject {
	static ObcItemStack asCraftMirror(NmsItemStack nms) {
		return WrappedObject.getStatic(ObcItemStack.class).staticAsCraftMirror(nms);
	}

	static ObcItemStack ensure(ItemStack item) {
		if (item == null) {
			item = new ItemStack(Material.AIR);
		}
		if (Objects.requireNonNull(WrappedObject.getRawClass(ObcItemStack.class)).isAssignableFrom(item.getClass())) {
			return WrappedObject.wrap(ObcItemStack.class, item);
		} else {
			return asCraftCopy(item);
		}
	}

	static ObcItemStack asCraftCopy(ItemStack item) {
		return WrappedObject.getStatic(ObcItemStack.class).staticAsCraftCopy(item);
	}

	static boolean isAir(ItemStack is) {
		return getCount(is) < 1;
	}

	static int getCount(ItemStack is) {
		if (is == null || is.getType() == Material.AIR) {
			return 0;
		}
		return is.getAmount();
	}

	static ItemStack asBukkitCopy(NmsItemStack nms) {
		return WrappedObject.getStatic(ObcItemStack.class).staticAsBukkitCopy(nms);
	}

	static NmsItemStack asNMSCopy(ItemStack is) {
		return WrappedObject.getStatic(ObcItemStack.class).staticAsNMSCopy(is);
	}

	@WrappedMethod("asCraftMirror")
	ObcItemStack staticAsCraftMirror(NmsItemStack nms);

	@WrappedMethod("asCraftCopy")
	ObcItemStack staticAsCraftCopy(ItemStack item);

	@WrappedMethod("asBukkitCopy")
	ItemStack staticAsBukkitCopy(NmsItemStack nms);

	@WrappedMethod("asNMSCopy")
	NmsItemStack staticAsNMSCopy(ItemStack item);

	@WrappedFieldAccessor("handle")
	NmsItemStack getHandle();

	@WrappedFieldAccessor("handle")
	ObcItemStack setHandle(NmsItemStack handle);

	@Override
	ItemStack getRaw();
}
