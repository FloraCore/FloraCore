package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass(@VersionName("obc.enchantments.CraftEnchantment"))
public interface ObcEnchantment extends WrappedBukkitObject {
    @WrappedMethod("getHandle")
    NmsEnchantment getHandle();
}
