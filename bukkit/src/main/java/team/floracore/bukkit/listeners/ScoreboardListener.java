package team.floracore.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.event.BodyUpdateEvent;
import team.floracore.bukkit.event.TitleUpdateEvent;
import team.floracore.bukkit.listener.FloraCoreBukkitListener;
import team.floracore.bukkit.scoreboard.model.BoardModel;
import team.floracore.bukkit.util.BukkitStringReplacer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 计分板事件监听
 *
 * @author xLikeWATCHDOG
 */
public class ScoreboardListener extends FloraCoreBukkitListener {
	public static List<String> offList = new ArrayList<>();
	private final FCBukkitPlugin plugin;

	public ScoreboardListener(FCBukkitPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	public static boolean check(final Player player, final BoardModel model) {
		return player.hasPermission(model.permission) && dataCheck(model) && !offList.contains(player.getName());
	}

	public static boolean dataCheck(final BoardModel model) {
		final long now = System.currentTimeMillis();
		return (model.time_start == null || model.time_start.getTime() <= now) && (model.time_end == null || now <= model.time_end.getTime());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBodyUpdate(BodyUpdateEvent event) {
		for (BoardModel model : plugin.getScoreBoardManager().getModels()) {
			if (check(event.getPlayer(), model)) {
				List<String> lines = BukkitStringReplacer.processStringListForPlayer(event.getPlayer(), model.lines);
				List<String> temp = new ArrayList<>();
				lines.forEach(s -> temp.addAll(Arrays.asList(s.split("\n"))));
				event.setBody(temp);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onTitleUpdate(TitleUpdateEvent event) {
		for (BoardModel model : plugin.getScoreBoardManager().getModels()) {
			if (check(event.getPlayer(), model)) {
				event.setTitle(BukkitStringReplacer.processStringForPlayer(event.getPlayer(), model.title));
				break;
			}
		}
	}
}
