package team.floracore.bukkit.util.wrappednms;

import org.bukkit.entity.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrappedmojang.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

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
