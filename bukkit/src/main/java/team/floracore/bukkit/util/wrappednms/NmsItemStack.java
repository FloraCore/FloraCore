package team.floracore.bukkit.util.wrappednms;


import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.ItemStack", maxVer = 17), @VersionName(value = "net.minecraft.world.item.ItemStack", minVer = 17)})
public interface NmsItemStack extends WrappedBukkitObject {
    static NmsItemStack fromNbtV13(NmsNBTTagCompound nbt) {
        return WrappedObject.getStatic(NmsItemStack.class).staticFromNbtV13(nbt);
    }

    static NmsItemStack newInstanceV12_13(NmsNBTTagCompound nbt) {
        return WrappedObject.getStatic(NmsItemStack.class).staticNewInstanceV12_13(nbt);
    }

    static NmsItemStack fromNbt(NmsNBTTagCompound nbt) {
        if (BukkitWrapper.v13)
            return fromNbtV13(nbt);
        else
            return newInstanceV12_13(nbt);
    }

    static NmsItemStack newInstance(NmsItem item, int count) {
        if (BukkitWrapper.v13)
            return WrappedObject.getStatic(NmsItemStack.class).staticNewInstanceV13(item.cast(NmsIMaterialV13.class), count);
        else
            return WrappedObject.getStatic(NmsItemStack.class).staticNewInstanceV12_13(item, count);
    }

    static NmsItemStack newInstance(NmsItem item) {
        return newInstance(item, 1);
    }

    @WrappedBukkitConstructor(minVer = 12, maxVer = 13)
    NmsItemStack staticNewInstanceV12_13(NmsItem item, int count);

    @WrappedBukkitConstructor(minVer = 13)
    NmsItemStack staticNewInstanceV13(NmsIMaterialV13 item, int count);

    @WrappedBukkitMethod(@VersionName(value = {"fromCompound", "a"}, minVer = 13))
    NmsItemStack staticFromNbtV13(NmsNBTTagCompound nbt);

    @WrappedBukkitConstructor(minVer = 12, maxVer = 13)
    NmsItemStack staticNewInstanceV12_13(NmsNBTTagCompound nbt);

    @WrappedMethod({"save", "b"})
    NmsNBTTagCompound save(NmsNBTTagCompound nbt);

    @WrappedBukkitFieldAccessor({@VersionName("item"), @VersionName(value = "@0", minVer = 17)})
    NmsItem getItem();

    @WrappedBukkitFieldAccessor({@VersionName("item"), @VersionName(value = "@0", minVer = 17)})
    NmsItemStack setItem(NmsItem item);

    @WrappedBukkitFieldAccessor({@VersionName("tag"), @VersionName(value = "@0", minVer = 17)})
    NmsNBTTagCompound getTag();

    @WrappedBukkitFieldAccessor({@VersionName("tag"), @VersionName(value = "@0", minVer = 17)})
    NmsItemStack setTag(NmsNBTTagCompound tag);

    @WrappedBukkitMethod({@VersionName("cloneItemStack"), @VersionName(value = "m", minVer = 18, maxVer = 18.2f), @VersionName(value = "n", minVer = 18.2f, maxVer = 19), @VersionName(value = "o", minVer = 19)})
    NmsItemStack cloneItemStack();
}
