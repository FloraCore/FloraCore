package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.EntityItem", maxVer = 17), @VersionName(value = "net.minecraft.world.entity.item.EntityItem", minVer = 17)})
public interface NmsEntityItem extends NmsEntity {
    static NmsDataWatcherObject getItemStackType() {
        return WrappedObject.getStatic(NmsEntityItem.class).staticGetItemStackType();
    }

    @WrappedBukkitFieldAccessor(@VersionName({"ITEM", "#0"}))
    NmsDataWatcherObject staticGetItemStackType();
}
