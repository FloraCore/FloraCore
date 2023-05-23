package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.EntityHuman", maxVer = 17), @VersionName(value = "net.minecraft.world.entity.player.EntityHuman", minVer = 17)})
public interface NmsEntityHuman extends NmsEntityLiving {
    static NmsEntityHuman fromBukkit(HumanEntity player) {
        return WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityHuman.class);
    }

    @WrappedBukkitFieldAccessor(@VersionName(value = "defaultContainer", maxVer = 14))
    NmsContainer getPlayerContainerV_14();

    @WrappedBukkitFieldAccessor({@VersionName(value = "defaultContainer", minVer = 14), @VersionName(minVer = 17, value = "@0")})
    NmsContainerPlayer getPlayerContainerV14();

    default NmsContainer getPlayerContainer() {
        if (BukkitWrapper.version >= 14)
            return getPlayerContainerV14();
        else
            return getPlayerContainerV_14();
    }

    @WrappedBukkitFieldAccessor({@VersionName("activeContainer"), @VersionName(value = "@1", maxVer = 14), @VersionName(value = "@0", minVer = 14)})
    NmsContainer getOpenContainer();

    @WrappedBukkitMethod({@VersionName("drop"), @VersionName(minVer = 18, value = "a")})
    NmsEntityItem drop(NmsItemStack item, boolean setThrower);
}
