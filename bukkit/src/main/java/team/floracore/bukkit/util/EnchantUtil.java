package team.floracore.bukkit.util;

import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.plugin.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;
import java.util.Map.*;

public final class EnchantUtil {
	public static Map<Integer, Enchantment> byId;
	public static Map<String, Enchantment> byName;
	public static Map<NamespacedKey, Enchantment> byKey;
	public static Map<?, Integer> IdMap;
	public static Map<?, ?> keyMap;
	public static Map<?, ?> resourceKeyMap;
	public static Map<?, ?> lifecycleMap;
	public static Object regId;
	public static Map<Entry<Plugin, String>, Enchantment> regEnchants = new HashMap<>();
	public static Map<Integer, String> romanNums = new LinkedHashMap<>();

	static {
		try {
			byName = WrappedEnchantment.getByName();
			if (BukkitWrapper.v13)
				byKey = WrappedEnchantment.getByKeyV13();
			else
				byId = WrappedEnchantment.getByIdV_13();
			if (BukkitWrapper.v13) {
				try {
					keyMap = NmsIRegistry.getEnchantsV13().cast(NmsRegistryMaterials.class).getKeyMapV13();
					resourceKeyMap = NmsIRegistry.getEnchantsV13().cast(NmsRegistryMaterials.class).getResourceKeyMapV16();
					lifecycleMap = NmsIRegistry.getEnchantsV13().cast(NmsRegistryMaterials.class).getLifecycleMapV16();
					IdMap = NmsIRegistry.getEnchantsV13().cast(NmsRegistryMaterials.class).getIdMapV16();
				} catch (Throwable e) {
					try {
						regId = NmsIRegistry.getEnchantsV13().cast(NmsRegistryMaterials.class).getRegIDV_15();
					} catch (Throwable e1) {
						throw e;
					}
				}
			}
		} catch (Throwable e) {
			throw TypeUtil.throwException(e);
		}
	}

	static {
		romanNums.put(1000, "M");
		romanNums.put(900, "CM");
		romanNums.put(500, "D");
		romanNums.put(400, "CD");
		romanNums.put(100, "C");
		romanNums.put(90, "XC");
		romanNums.put(50, "L");
		romanNums.put(40, "XL");
		romanNums.put(10, "X");
		romanNums.put(9, "IX");
		romanNums.put(5, "V");
		romanNums.put(4, "IV");
		romanNums.put(1, "I");
	}

	public @Deprecated EnchantUtil() {
	}

	public static String getEnchantId(Enchantment enchant) {
		return getEnchantKey(enchant).toString();
	}

	public static NamespacedKey getEnchantKey(Enchantment enchant) {
		if (enchant instanceof EnchantmentWrapper)
			enchant = ((EnchantmentWrapper) enchant).getEnchantment();
		return getEnchantKey0(enchant);
	}

	public static NamespacedKey getEnchantKey0(Enchantment enchant) {
		if (WrappedObject.getRawClass(ObcEnchantment.class).isAssignableFrom(enchant.getClass())) {
			NmsEnchantment nms = WrappedObject.wrap(ObcEnchantment.class, enchant).getHandle();
			if (nms.getRaw() != null) {
				if (BukkitWrapper.v13)
					return NmsIRegistry.getEnchantsV13().getKey(nms).toBukkit();
				else
					return NmsEnchantment.getEnchantsV_13().getKey(nms.cast(NmsEnchantment.class)).toBukkit();
			}
		}
		return NmsMinecraftKey.newInstance((enchant.getName().trim().isEmpty() ? enchant.getClass().getSimpleName() : enchant.getName()).toLowerCase()).toBukkit();
	}

	public static Enchantment getEnchant(NamespacedKey key) {
		return byKey.get(key);
	}

	public static Enchantment getEnchant(String id) {
		return byName.get(id);
	}

	public static Enchantment getEnchant(int id) {
		return byId.get(id);
	}

	@SuppressWarnings("deprecation")
	public static void unregEnchant(Plugin plugin, String name) {
		Enchantment enchant = regEnchants.get(new MapEntry<>(plugin, name));
		regEnchants.remove(new MapEntry<>(plugin, name));
		byName.remove(enchant.getName());
		if (byId != null)
			byId.remove(enchant.getId());
		if (byKey != null)
			try {
				byKey.remove(WrappedObject.wrap(WrappedEnchantment.class, enchant).getKeyV13());
			} catch (Throwable e) {
				throw TypeUtil.throwException(e);
			}
		if (BukkitWrapper.v13) {
			Object nms = getNms(new NamespacedKey(plugin, name));
			if (IdMap != null)
				IdMap.remove(nms);
			NmsMinecraftKey key = NmsMinecraftKey.newInstance(plugin.getName().toLowerCase(), name.toLowerCase());
			keyMap.remove(key.getRaw());
			if (resourceKeyMap != null)
				resourceKeyMap.remove(NmsResourceKey.fromKeyV13(key).getRaw());
			if (lifecycleMap != null)
				lifecycleMap.remove(nms);
		}
	}

	public static Object getNms(NamespacedKey key) {
		if (key == null)
			return null;
		if (BukkitWrapper.v13)
			return NmsIRegistry.getEnchantsV13().cast(NmsIRegistry.class).get(NmsMinecraftKey.newInstance(key)).getRaw();
		else
			return NmsEnchantment.getEnchantsV_13().cast(NmsRegistrySimpleV_13.class).get(NmsMinecraftKey.newInstance(key)).getRaw();
	}

	public static Object getNms(short id) {
		if (BukkitWrapper.v13)
			return NmsIRegistry.getEnchantsV13().cast(NmsRegistryMaterials.class).fromId(id);
		else
			return NmsEnchantment.getEnchantsV_13().fromId(id).getRaw();
	}

	public static String getTranslateKey(NamespacedKey key) {
		Object nms = getNms(key);
		if (nms != null)
			return getTranslateKey(nms);
		else
			return "enchantment." + key.getNamespace() + '.' + key.getKey();
	}

	public static String getTranslateKey(Object nmsEnchant) {
		if (nmsEnchant == null)
			return "enchantment.null";
		return WrappedObject.wrap(NmsEnchantment.class, nmsEnchant).getTranslateKey();
	}

	public static String getRomanNum(int level) {
		StringBuilder sb1000 = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		if (level > 4999) {
			for (Entry<Integer, String> e : romanNums.entrySet()) {
				if (e.getKey() < 5)
					break;
				while (e.getKey() * 1000 <= level) {
					level -= e.getKey() * 1000;
					sb1000.append(e.getValue());
				}
			}
		}
		for (Entry<Integer, String> e : romanNums.entrySet()) {
			while (e.getKey() <= level) {
				level -= e.getKey();
				sb.append(e.getValue());
			}
		}
		if (sb1000.length() > 0)
			return '(' + sb1000.toString() + ')' + sb;
		else
			return sb.toString();
	}

	public static String getEnchantLevel(int level) {
		return level < 1 || level > 50 ? Integer.valueOf(level).toString() : getRomanNum(level);
	}

	public static boolean filterProxyEnchant(Enchantment enchant) {
		return enchant != null;
	}

}
