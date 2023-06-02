package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitObject;

@WrappedBukkitClass({@VersionName(value = "nms.IBlockData",
        maxVer = 17), @VersionName(value = "net.minecraft.world.level.block.state.IBlockData",
        minVer = 17)})
public interface NmsIBlockData extends WrappedBukkitObject {
}