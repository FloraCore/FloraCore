package team.floracore.bukkit.util.wrappedobc;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappednms.NmsEnchantment;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;

@WrappedBukkitClass(@VersionName("obc.enchantments.CraftEnchantment"))
public interface ObcEnchantment extends WrappedBukkitObject {
	@WrappedMethod("getHandle")
	NmsEnchantment getHandle();
}
