package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.Optional;
import team.floracore.common.util.TypeUtil;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@WrappedBukkitClass({@VersionName(value = "nms.CraftingManager", maxVer = 17),
		@VersionName(value = "net.minecraft.world.item.crafting.CraftingManager", minVer = 17)})
public interface NmsCraftingManager extends WrappedBukkitObject {
	static Map<Object, Object> getCraftingRecipes() {
		if (BukkitWrapper.version >= 14) {
			return NmsMinecraftServer.getServer().getCraftingManagerV13().getRecipesV14().get(NmsRecipesV14.crafting());
		} else if (BukkitWrapper.v13) {
			return NmsMinecraftServer.getServer().getCraftingManagerV13().getRecipes0V13_14().getRaw();
		} else {
			return getRecipesV_13();
		}
	}

	static Map<Object, Object> getRecipesV_13() {
		return WrappedObject.wrap(NmsCraftingManager.class, null)
				.getRecipes0V_13()
				.cast(NmsRegistrySimpleV_13.class)
				.getMap();
	}

	static void addRecipe(NmsMinecraftKey key, NmsIRecipe recipe) {
		if (BukkitWrapper.v13) {
			NmsMinecraftServer.getServer().getCraftingManagerV13().addRecipe0V13(recipe);
		} else {
			addRecipeV_13(key, recipe);
		}
	}

	static void addRecipeV_13(NmsMinecraftKey key, NmsIRecipe recipe) {
		WrappedObject.wrap(NmsCraftingManager.class, null).staticAddRecipeV_13(key, recipe);
	}

	static Map<Object, Object> getBlastingRecipesV13() {
		if (BukkitWrapper.v13) {
			return NmsMinecraftServer.getServer().getCraftingManagerV13().getRecipesV14().get(NmsRecipesV14.blasting());
		} else {
			return getRecipesV_13();
		}
	}

	static Map<Object, Object> getCampfireCookingRecipesV13() {
		if (BukkitWrapper.v13) {
			return NmsMinecraftServer.getServer()
					.getCraftingManagerV13()
					.getRecipesV14()
					.get(NmsRecipesV14.campfireCooking());
		} else {
			return getRecipesV_13();
		}
	}

	static Map<Object, Object> getSmeltingRecipesV13() {
		return NmsMinecraftServer.getServer().getCraftingManagerV13().getRecipesV14().get(NmsRecipesV14.smelting());
	}

	static Map<Object, Object> getSmithingRecipesV16() {
		return NmsMinecraftServer.getServer().getCraftingManagerV13().getRecipesV14().get(NmsRecipesV14.smithingV16());
	}

	static Map<Object, Object> getSmokingRecipesV13() {
		if (BukkitWrapper.v13) {
			return NmsMinecraftServer.getServer().getCraftingManagerV13().getRecipesV14().get(NmsRecipesV14.smoking());
		} else {
			return getRecipesV_13();
		}
	}

	static Map<Object, Object> getStonecuttingRecipesV13() {
		if (BukkitWrapper.v13) {
			return NmsMinecraftServer.getServer()
					.getCraftingManagerV13()
					.getRecipesV14()
					.get(NmsRecipesV14.stonecutting());
		} else {
			return getRecipesV_13();
		}
	}

	default Map<NmsRecipesV14, Map<Object, Object>> getRecipesV14() {
		Map<Object, Map<Object, Object>> raw = getRecipes0V14();
		return new AbstractMap<NmsRecipesV14, Map<Object, Object>>() {
			@Override
			public Map<Object, Object> put(NmsRecipesV14 key, Map<Object, Object> value) {
				return raw.put(key.getRaw(), value);
			}

			@Override
			public Set<Entry<NmsRecipesV14, Map<Object, Object>>> entrySet() {
				Set<Entry<Object, Map<Object, Object>>> r = TypeUtil.cast(raw.entrySet());
				return new AbstractSet<Entry<NmsRecipesV14, Map<Object, Object>>>() {
					@Override
					public Iterator<Entry<NmsRecipesV14, Map<Object, Object>>> iterator() {
						Iterator<Entry<Object, Map<Object, Object>>> i = r.iterator();
						return new Iterator<Entry<NmsRecipesV14, Map<Object, Object>>>() {
							@Override
							public boolean hasNext() {
								return i.hasNext();
							}

							@Override
							public Entry<NmsRecipesV14, Map<Object, Object>> next() {
								Entry<?, Map<Object, Object>> e = i.next();
								if (e == null) {
									return null;
								}
								return new Entry<NmsRecipesV14, Map<Object, Object>>() {
									@Override
									public NmsRecipesV14 getKey() {
										return WrappedObject.wrap(NmsRecipesV14.class, e.getKey());
									}

									@Override
									public Map<Object, Object> getValue() {
										return e.getValue();
									}

									@Override
									public Map<Object, Object> setValue(Map<Object, Object> value) {
										return e.setValue(value);
									}
								};
							}
						};
					}

					@Override
					public int size() {
						return r.size();
					}
				};
			}
		};
	}

	@WrappedBukkitFieldAccessor(@VersionName(minVer = 13, value = "recipes", maxVer = 14))
	WrappedObject2ObjectLinkedOpenHashMapV13 getRecipes0V13_14();

	@WrappedBukkitFieldAccessor({@VersionName(minVer = 14, value = "recipes"),
			@VersionName(minVer = 17, value = "@0")})
	Map<Object, Map<Object, Object>> getRecipes0V14();

	@WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "recipes"))
	NmsRegistryMaterials getRecipes0V_13();

	@WrappedBukkitMethod({@VersionName(minVer = 13, value = "addRecipe"),
			@VersionName(value = "@0", minVer = 13, maxVer = 14)})
	void addRecipe0V13(NmsIRecipe recipe);

	@WrappedBukkitMethod(@VersionName(maxVer = 13, value = "#0"))
	void staticAddRecipeV_13(NmsMinecraftKey key, NmsIRecipe recipe);

	@Optional
	@WrappedBukkitFieldAccessor(@VersionName("ALL_RECIPES_CACHE"))
	Collection<Object> getRecipesCache();

	@WrappedBukkitFieldAccessor(@VersionName(minVer = 18, value = "d"))
	Map<Object, Object> getRecipesV18();
}
