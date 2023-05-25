package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.Recipes", minVer = 14, maxVer = 17), @VersionName(value = "net.minecraft.world.item.crafting.Recipes", minVer = 17)})
public interface NmsRecipesV14 extends WrappedBukkitObject {
    static NmsRecipesV14 crafting() {
        return WrappedObject.getStatic(NmsRecipesV14.class).staticCrafting();
    }

    @WrappedBukkitFieldAccessor({@VersionName("CRAFTING"), @VersionName(minVer = 17, value = "a")})
    NmsRecipesV14 staticCrafting();

    static NmsRecipesV14 smelting() {
        return WrappedObject.getStatic(NmsRecipesV14.class).staticSmelting();
    }

    @WrappedBukkitFieldAccessor({@VersionName("SMELTING"), @VersionName(minVer = 17, value = "b")})
    NmsRecipesV14 staticSmelting();

    static NmsRecipesV14 blasting() {
        return WrappedObject.getStatic(NmsRecipesV14.class).staticBlasting();
    }

    @WrappedBukkitFieldAccessor({@VersionName("BLASTING"), @VersionName(minVer = 17, value = "c")})
    NmsRecipesV14 staticBlasting();

    static NmsRecipesV14 smoking() {
        return WrappedObject.getStatic(NmsRecipesV14.class).staticSmoking();
    }

    @WrappedBukkitFieldAccessor({@VersionName("SMOKING"), @VersionName(minVer = 17, value = "d")})
    NmsRecipesV14 staticSmoking();

    static NmsRecipesV14 campfireCooking() {
        return WrappedObject.getStatic(NmsRecipesV14.class).staticCampfireCooking();
    }

    @WrappedBukkitFieldAccessor({@VersionName("CAMPFIRE_COOKING"), @VersionName(minVer = 17, value = "e")})
    NmsRecipesV14 staticCampfireCooking();

    static NmsRecipesV14 stonecutting() {
        return WrappedObject.getStatic(NmsRecipesV14.class).staticStonecutting();
    }

    @WrappedBukkitFieldAccessor({@VersionName("STONECUTTING"), @VersionName(minVer = 17, value = "f")})
    NmsRecipesV14 staticStonecutting();

    static NmsRecipesV14 smithingV16() {
        return WrappedObject.getStatic(NmsRecipesV14.class).staticSmithingV16();
    }

    @WrappedBukkitFieldAccessor({@VersionName(value = "SMITHING", minVer = 16), @VersionName(minVer = 17, value = "g")})
    NmsRecipesV14 staticSmithingV16();
}
