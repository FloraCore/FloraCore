package team.floracore.bukkit.listener.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import team.floracore.bukkit.FCBukkitPlugin;
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
		if (plugin.getConfiguration().get(ConfigKeys.MODULE_JOIN_MESSAGE_ENABLE)) {
			Player p = e.getPlayer();
			String jm = plugin.getConfiguration().get(ConfigKeys.MODULE_JOIN_MESSAGE);
			jm = jm.replace("%player%", p.getName());
			jm = BukkitStringReplacer.processStringForPlayer(p, jm);
			e.setJoinMessage(jm);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (plugin.getConfiguration().get(ConfigKeys.MODULE_QUIT_MESSAGE_ENABLE)) {
			Player p = e.getPlayer();
			String qm = plugin.getConfiguration().get(ConfigKeys.MODULE_QUIT_MESSAGE);
			qm = qm.replace("%player%", p.getName());
			qm = BukkitStringReplacer.processStringForPlayer(p, qm);
			e.setQuitMessage(qm);
		}
	}


	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if (plugin.getConfiguration().get(ConfigKeys.MODULE_NO_WEATHER)) {
			if (e.toWeatherState()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void entityDamageEvent(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			EntityDamageEvent.DamageCause damageCause = event.getCause();
			if (damageCause == EntityDamageEvent.DamageCause.FALL) {
				if (plugin.getConfiguration().get(ConfigKeys.MODULE_NO_FALL)) {
					event.setCancelled(true);
				}
			} else if (damageCause == EntityDamageEvent.DamageCause.FIRE || damageCause == EntityDamageEvent.DamageCause.FIRE_TICK) {
				if (plugin.getConfiguration().get(ConfigKeys.MODULE_NO_FIRE_DAMAGE)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void noHungry(FoodLevelChangeEvent e) {
		if (e.getEntityType() != EntityType.PLAYER) {
			return;
		}
		Player player = (Player) e.getEntity();
		if (plugin.getConfiguration().get(ConfigKeys.MODULE_NO_HUNGRY)) {
			player.setFoodLevel(20);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (plugin.getConfiguration().get(ConfigKeys.MODULE_DEATH_MESSAGE_ENABLE)) {
			Player p = e.getEntity();
			String dm = plugin.getConfiguration().get(ConfigKeys.MODULE_DEATH_MESSAGE);
			dm = dm.replace("%player%", p.getName());
			dm = BukkitStringReplacer.processStringForPlayer(p, dm);
			e.setDeathMessage(dm);
		}
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent e) {
		if (!plugin.getConfiguration().get(ConfigKeys.MODULE_MOB_SPAWN)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		if (!plugin.getConfiguration().get(ConfigKeys.MODULE_PLACE_BLOCK)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent e) {
		if (!plugin.getConfiguration().get(ConfigKeys.MODULE_BREAK_BLOCK)) {
			e.setCancelled(true);
		}
	}
}
