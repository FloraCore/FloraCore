package team.floracore.bukkit.util;

import com.google.common.io.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import team.floracore.bukkit.*;

/**
 * 关于BungeeCord的操作类
 */
public class BungeeUtil {
    private final FCBukkitPlugin plugin;

    public BungeeUtil(FCBukkitPlugin plugin) {
        this.plugin = plugin;
        Plugin p = plugin.getLoader();
        Bukkit.getMessenger().registerOutgoingPluginChannel(p, "BungeeCord");
    }

    public void connect(Player player, String serverName) {
        Plugin p = plugin.getLoader();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(p, "BungeeCord", out.toByteArray());
    }
}
