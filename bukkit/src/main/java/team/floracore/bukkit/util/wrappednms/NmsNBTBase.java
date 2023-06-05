package team.floracore.bukkit.util.wrappednms;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.List;
import java.util.Objects;

@WrappedBukkitClass({@VersionName(value = "nms.NBTBase", maxVer = 17),
        @VersionName(value = "net.minecraft.nbt.NBTBase", minVer = 17)})
public interface NmsNBTBase extends WrappedBukkitObject {
    List<Class<? extends NmsNBTBase>> NBTWrappers = Lists.newArrayList(NmsNBTTagByte.class,
            NmsNBTTagCompound.class,
            NmsNBTTagInt.class,
            NmsNBTTagList.class,
            NmsNBTTagLong.class,
            NmsNBTTagShort.class,
            NmsNBTTagDouble.class,
            NmsNBTTagString.class);

    static NmsNBTBase wrap(Object nmsNbt) {
        for (Class<? extends NmsNBTBase> w : NBTWrappers) {
            if (Objects.requireNonNull(WrappedObject.getRawClass(w)).isAssignableFrom(nmsNbt.getClass())) {
                return WrappedObject.wrap(w, nmsNbt);
            }
        }
        throw new IllegalArgumentException("unknown type " + nmsNbt.getClass() + " of NBTBase");
    }

    JsonElement toJson();
}
