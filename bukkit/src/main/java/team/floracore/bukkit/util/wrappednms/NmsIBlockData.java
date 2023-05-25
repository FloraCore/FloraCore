package team.floracore.bukkit.util.wrappednms;


import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.IBlockData", maxVer = 17), @VersionName(value = "net.minecraft.world.level.block.state.IBlockData", minVer = 17)})
public interface NmsIBlockData extends WrappedBukkitObject {
}