package team.floracore.bukkit.util.wrappedobc;

import org.bukkit.*;
import org.bukkit.inventory.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

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
