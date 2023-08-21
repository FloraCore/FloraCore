package team.floracore.bukkit.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.floracore.api.FloraCore;
import org.floracore.api.FloraCoreProvider;
import org.floracore.api.data.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.common.locale.translation.TranslationManager;

import java.util.UUID;

/**
 * @author xLikeWATCHDOG
 */
public class PlaceholderAPIHook extends PlaceholderExpansion {
	private final FCBukkitPlugin plugin;

	public PlaceholderAPIHook(FCBukkitPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public @NotNull String getIdentifier() {
		return plugin.getLoader().getDescription().getName();
	}

	@Override
	public @NotNull String getAuthor() {
		return plugin.getLoader().getDescription().getAuthors().toString();
	}

	@Override
	public @NotNull String getVersion() {
		return plugin.getLoader().getDescription().getVersion();
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String args) {
		FloraCore floraCore = FloraCoreProvider.get();
		String unknown = "unknown";
		if (player == null) {
			return unknown;
		}
		UUID uuid = player.getUniqueId();
		if (args.startsWith("translation")) {
			String[] parts = args.split("#");
			if (parts.length >= 2) {
				String path = parts[1];
				Component component = Component.translatable().key(path).build();
				Component replacement = TranslationManager.render(component, uuid);
				return TranslationManager.SERIALIZER.serialize(replacement);
			}
		} else if (args.startsWith("data")) {
			String[] parts = args.split("#|\\$");
			if (parts.length >= 4) {
				String type = parts[1];
				String key = parts[3];
				try {
					DataType dataType = DataType.valueOf(type);
					return floraCore.getDataAPI().getSpecifiedDataValue(uuid, dataType, key);
				} catch (IllegalArgumentException e) {
					return unknown;
				}
			}
		} else if (args.startsWith("intdata")) {
			String[] parts = args.split("#|\\$");
			if (parts.length >= 4) {
				String type = parts[1];
				String key = parts[3];
				try {
					DataType dataType = DataType.valueOf(type);
					return floraCore.getDataAPI().getSpecifiedDataValue(uuid, dataType, key);
				} catch (IllegalArgumentException e) {
					return unknown;
				}
			}
		}
		return unknown;
	}
}
