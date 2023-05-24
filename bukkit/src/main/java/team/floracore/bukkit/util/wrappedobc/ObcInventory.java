package team.floracore.bukkit.util.wrappedobc;

import org.bukkit.inventory.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass(@VersionName("obc.inventory.CraftInventory"))
public interface ObcInventory extends WrappedBukkitObject {
    @WrappedMethod("getInventory")
    NmsIInventory getNms();

    @Override
    Inventory getRaw();
}
