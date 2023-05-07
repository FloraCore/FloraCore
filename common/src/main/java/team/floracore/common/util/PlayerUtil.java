package team.floracore.common.util;

import org.bukkit.*;
import org.bukkit.entity.*;

import java.security.*;
import java.util.*;

public class PlayerUtil {
    public static Player getRandomPlayer() {
        return Bukkit.getOnlinePlayers().size() > 0 ? new ArrayList<>(Bukkit.getOnlinePlayers()).get(new SecureRandom().nextInt(Bukkit.getOnlinePlayers().size())) : null;
    }
}
