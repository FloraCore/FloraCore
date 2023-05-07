package team.floracore.common.util;

import com.google.common.io.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import team.floracore.common.plugin.*;

/**
 * 关于BungeeCord的操作类
 */
public class BungeeUtil {
    private final FloraCorePlugin plugin;

    public BungeeUtil(FloraCorePlugin plugin) {
        this.plugin = plugin;
        Plugin p = plugin.getBootstrap().getPlugin();
        Bukkit.getMessenger().registerOutgoingPluginChannel(p, "BungeeCord");
    }

    public void connect(Player player, String serverName) {
        Plugin p = this.plugin.getBootstrap().getPlugin();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(p, "BungeeCord", out.toByteArray());
    }
}
