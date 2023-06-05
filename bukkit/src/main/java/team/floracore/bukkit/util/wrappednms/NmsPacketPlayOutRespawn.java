package team.floracore.bukkit.util.wrappednms;

import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitConstructor;
import team.floracore.common.util.wrapper.WrappedObject;

@WrappedBukkitClass({@VersionName(value = "nms.PacketPlayOutRespawn", maxVer = 17),
        @VersionName(value = "net.minecraft.network.protocol.game.PacketPlayOutRespawn", minVer = 17)})
public interface NmsPacketPlayOutRespawn extends NmsPacket {
    static NmsPacketPlayOutRespawn newInstance(int var1,
                                               NmsEnumDifficulty var2,
                                               NmsWorldType var3,
                                               NmsEnumGamemode var4) {
        return WrappedObject.getStatic(NmsPacketPlayOutRespawn.class)
                .staticNewInstance(var1, var2, var3, var4);
    }

    @WrappedBukkitConstructor
    NmsPacketPlayOutRespawn staticNewInstance(int var1,
                                              NmsEnumDifficulty var2,
                                              NmsWorldType var3,
                                              NmsEnumGamemode var4);
}
