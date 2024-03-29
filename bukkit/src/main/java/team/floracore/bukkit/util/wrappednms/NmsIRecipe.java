package team.floracore.bukkit.util.wrappednms;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappedobc.ObcItemStack;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedMethod;

@WrappedBukkitClass({@VersionName(value = "nms.IRecipe", maxVer = 17),
		@VersionName(value = "net.minecraft.world.item.crafting.IRecipe", minVer = 17)})
public interface NmsIRecipe extends WrappedBukkitObject, Recipe {
	@WrappedMethod("toBukkitRecipe")
	Recipe toBukkitRecipe();

	default NmsItemStack getResult(NmsIInventory inv) {
		if (BukkitWrapper.v13) {
			return getResultV13(inv);
		} else {
			return getResultV_13(inv.cast(NmsInventoryCrafting.class));
		}
	}

	@WrappedBukkitMethod({@VersionName(value = "craftItem", minVer = 13, maxVer = 14),
			@VersionName(minVer = 14, value = "a")})
	NmsItemStack getResultV13(NmsIInventory inv);

	@WrappedBukkitMethod(@VersionName(maxVer = 13, value = "craftItem"))
	NmsItemStack getResultV_13(NmsInventoryCrafting inv);

	@Override
	default ItemStack getResult() {
		return ObcItemStack.asBukkitCopy(getResult0());
	}

	@WrappedBukkitMethod({@VersionName("getResult"),
			@VersionName(maxVer = 13, value = "b"),
			@VersionName(value = "d", minVer = 13, maxVer = 14),
			@VersionName(minVer = 14, maxVer = 16, value = "c"),
			@VersionName(minVer = 18, value = "c")})
	NmsItemStack getResult0();
}
