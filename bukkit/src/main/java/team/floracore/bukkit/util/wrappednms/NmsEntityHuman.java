package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.HumanEntity;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrappedmojang.WrappedGameProfile;
import team.floracore.bukkit.util.wrappedobc.ObcEntity;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.EntityHuman",
        maxVer = 17), @VersionName(value = "net.minecraft.world.entity.player.EntityHuman",
        minVer = 17)})
public interface NmsEntityHuman extends NmsEntityLiving {
    static NmsEntityHuman fromBukkit(HumanEntity player) {
        return WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityHuman.class);
    }

    @WrappedBukkitMethod(@VersionName("getProfile"))
    WrappedGameProfile getGameProfile();
}
