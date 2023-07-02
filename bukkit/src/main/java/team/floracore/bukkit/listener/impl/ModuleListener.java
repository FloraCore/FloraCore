package team.floracore.bukkit.listener.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.event.PlayerVoidDeathEvent;
import team.floracore.bukkit.listener.FloraCoreBukkitListener;
import team.floracore.bukkit.util.BukkitStringReplacer;
import team.floracore.common.config.ConfigKeys;

/**
 * @author xLikeWATCHDOG
 */
public class ModuleListener extends FloraCoreBukkitListener {
    private final FCBukkitPlugin plugin;

    public ModuleListener(FCBukkitPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String jm = plugin.getConfiguration().get(ConfigKeys.MODULE_JOIN_MESSAGE);
        jm = jm.replace("%player%", p.getName());
        jm = BukkitStringReplacer.processStringForPlayer(p, jm);
        e.setJoinMessage(jm);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String qm = plugin.getConfiguration().get(ConfigKeys.MODULE_QUIT_MESSAGE);
        qm = qm.replace("%player%", p.getName());
        qm = BukkitStringReplacer.processStringForPlayer(p, qm);
        e.setQuitMessage(qm);
    }


    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (plugin.getConfiguration().get(ConfigKeys.MODULE_NO_WEATHER)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (plugin.getConfiguration().get(ConfigKeys.MODULE_NO_FALL)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        double y = plugin.getConfiguration().get(ConfigKeys.MODULE_VOID_Y);
        boolean enableVoidDeath = plugin.getConfiguration().get(ConfigKeys.MODULE_VOID_SUICIDE);
        if (enableVoidDeath) {
            PlayerVoidDeathEvent playerVoidDeathEvent = new PlayerVoidDeathEvent(player);
            Bukkit.getPluginManager().callEvent(playerVoidDeathEvent);
            if (!playerVoidDeathEvent.isCancelled()) {
                if (player.getLocation().getY() <= y) {
                    player.setHealth(0);
                }
            }
        }
    }

    @EventHandler
    public void noHungry(FoodLevelChangeEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) e.getEntity();
        if (plugin.getConfiguration().get(ConfigKeys.MODULE_NO_HUNGRY)) {
            player.setFoodLevel(20);
            e.setCancelled(true);
        }
    }
}
