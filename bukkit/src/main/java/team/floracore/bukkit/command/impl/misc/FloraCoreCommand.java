package team.floracore.bukkit.command.impl.misc;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.github.benmanes.caffeine.cache.Cache;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.floracore.api.data.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.config.boards.BoardsKeys;
import team.floracore.bukkit.locale.message.commands.MiscCommandMessage;
import team.floracore.common.chat.ChatProvider;
import team.floracore.common.config.ConfigKeys;
import team.floracore.common.http.UnsuccessfulRequestException;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.CommonCommandMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.locale.translation.TranslationRepository;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;
import team.floracore.common.storage.misc.floracore.tables.PLAYER;
import team.floracore.common.util.CaffeineFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * FloraCore命令
 */
@CommandContainer
@CommandDescription("floracore.command.description.floracore")
public class FloraCoreCommand extends FloraCoreBukkitCommand {
	private static final Cache<UUID, List<DATA>> dataCache = CaffeineFactory.newBuilder()
			.expireAfterWrite(3, TimeUnit.SECONDS)
			.build();
	private static final Cache<String, UUID> uuidCache = CaffeineFactory.newBuilder()
			.expireAfterWrite(10, TimeUnit.SECONDS).build();
	private final FCBukkitPlugin plugin;

	public FloraCoreCommand(FCBukkitPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@CommandMethod("fc|floracore reload")
	@CommandDescription("floracore.command.description.floracore.reload")
	@CommandPermission("floracore.admin")
	public void reload(final @NotNull CommandSender sender) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		plugin.getConfiguration().reload();
		plugin.getBoardsConfiguration().reload();
		plugin.getTranslationManager().reload();
		if (plugin.getBoardsConfiguration().get(BoardsKeys.ENABLE)) {
			plugin.getScoreBoardManager().reload();
		}
		CommonCommandMessage.RELOAD_CONFIG_SUCCESS.send(s);
	}

	@CommandMethod("fc|floracore translations")
	@CommandDescription("floracore.command.description.floracore.translations")
	@CommandPermission("floracore.admin")
	public void translations(final @NotNull CommandSender sender) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		CommonCommandMessage.TRANSLATIONS_SEARCHING.send(s);

		List<TranslationRepository.LanguageInfo> availableTranslations;
		try {
			availableTranslations = plugin.getTranslationRepository().getAvailableLanguages();
		} catch (IOException | UnsuccessfulRequestException e) {
			CommonCommandMessage.TRANSLATIONS_SEARCHING_ERROR.send(s);
			plugin.getLogger().warn("Unable to obtain a list of available translations", e);
			return;
		}

		CommonCommandMessage.INSTALLED_TRANSLATIONS.send(s,
				plugin.getTranslationManager()
						.getInstalledLocales()
						.stream()
						.map(Locale::toLanguageTag)
						.sorted()
						.collect(Collectors.toList()));

		CommonCommandMessage.AVAILABLE_TRANSLATIONS_HEADER.send(s);
		availableTranslations.stream()
				.sorted(Comparator.comparing(language -> language.locale().toLanguageTag()))
				.forEach(language -> CommonCommandMessage.AVAILABLE_TRANSLATIONS_ENTRY.send(s,
						language.locale()
								.toLanguageTag(),
						TranslationManager.localeDisplayName(
								language.locale()),
						language.progress(),
						language.contributors()));
		s.sendMessage(AbstractMessage.prefixed(Component.empty()));
		CommonCommandMessage.TRANSLATIONS_DOWNLOAD_PROMPT.send(s);
	}

	@CommandMethod("fc|floracore translations install")
	@CommandDescription("floracore.command.description.floracore.translations.install")
	@CommandPermission("floracore.admin")
	public void installTranslations(final @NotNull CommandSender sender) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		CommonCommandMessage.TRANSLATIONS_SEARCHING.send(s);

		List<TranslationRepository.LanguageInfo> availableTranslations;
		try {
			availableTranslations = plugin.getTranslationRepository().getAvailableLanguages();
		} catch (IOException | UnsuccessfulRequestException e) {
			CommonCommandMessage.TRANSLATIONS_SEARCHING_ERROR.send(s);
			plugin.getLogger().warn("Unable to obtain a list of available translations", e);
			return;
		}
		CommonCommandMessage.TRANSLATIONS_INSTALLING.send(s);
		plugin.getTranslationRepository().downloadAndInstallTranslations(availableTranslations, s, true);
		CommonCommandMessage.TRANSLATIONS_INSTALL_COMPLETE.send(s);
	}

	@CommandMethod("fc|floracore data <target> <type>")
	@CommandDescription("floracore.command.description.floracore.data")
	@CommandPermission("floracore.admin")
	public void data(final @NotNull CommandSender sender,
	                 final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
	                 @Argument("type") DataType type) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		Player p = Bukkit.getPlayer(target);
		UUID u = uuidCache.getIfPresent(target.toLowerCase());
		if (u == null) {
			if (p == null) {
				try {
					PLAYER i = getStorageImplementation().selectPlayer(target);
					u = i.getUniqueId();
				} catch (Throwable e) {
					MiscMessage.PLAYER_NOT_FOUND.send(s, target);
					return;
				}
			} else {
				u = p.getUniqueId();
			}
			uuidCache.put(target.toLowerCase(), u);
		}
		List<DATA> all = dataCache.getIfPresent(u);
		if (all == null) {
			all = getStorageImplementation().selectData(u);
			dataCache.put(u, all);
		}
		List<DATA> ret = all.parallelStream().filter(data -> data.getType() == type).collect(Collectors.toList());
		if (ret.isEmpty()) {
			CommonCommandMessage.DATA_NONE.send(s, target);
		} else {
			CommonCommandMessage.DATA_HEADER.send(s, target);
			for (DATA data : ret) {
				MiscCommandMessage.DATA_ENTRY.send(s,
						data.getType().getName(),
						data.getKey(),
						data.getValue(),
						data.getExpiry());
			}
		}
	}

	@CommandMethod("fc|floracore data <target> set <key> <value>")
	@CommandDescription("floracore.command.description.floracore.data.set")
	@CommandPermission("floracore.admin")
	public void dataSet(final @NotNull CommandSender sender,
	                    final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
	                    final @NotNull @Argument("key") String key,
	                    final @NotNull @Argument("value") String value) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		Player p = Bukkit.getPlayer(target);
		UUID u = uuidCache.getIfPresent(target.toLowerCase());
		if (u == null) {
			if (p == null) {
				try {
					PLAYER i = getStorageImplementation().selectPlayer(target);
					u = i.getUniqueId();
				} catch (Throwable e) {
					MiscMessage.PLAYER_NOT_FOUND.send(s, target);
					return;
				}
			} else {
				u = p.getUniqueId();
			}
			uuidCache.put(target.toLowerCase(), u);
		}
		DATA data = getStorageImplementation().insertData(u, DataType.CUSTOM, key, value, 0);
		MiscCommandMessage.SET_DATA_SUCCESS.send(s, key, value, target);
	}

	@CommandMethod("fc|floracore data <target> unset <key>")
	@CommandDescription("floracore.command.description.floracore.data.unset")
	@CommandPermission("floracore.admin")
	public void dataUnSet(final @NotNull CommandSender sender,
	                      final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
	                      final @NotNull @Argument("key") String key) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		Player p = Bukkit.getPlayer(target);
		UUID u = uuidCache.getIfPresent(target.toLowerCase());
		if (u == null) {
			if (p == null) {
				try {
					PLAYER i = getStorageImplementation().selectPlayer(target);
					u = i.getUniqueId();
				} catch (Throwable e) {
					MiscMessage.PLAYER_NOT_FOUND.send(s, target);
					return;
				}
			} else {
				u = p.getUniqueId();
			}
			uuidCache.put(target.toLowerCase(), u);
		}
		DATA data = getStorageImplementation().getSpecifiedData(u, DataType.CUSTOM, key);
		if (data == null) {
			MiscCommandMessage.DOESNT_HAVE_DATA.send(s, target, key);
			return;
		}
		getStorageImplementation().deleteDataID(data.getId());
		MiscCommandMessage.UNSET_DATA_SUCCESS.send(s, key, target);
	}

	@CommandMethod("fc|floracore data <target> settemp <key> <value> <duration>")
	@CommandDescription("floracore.command.description.floracore.data.settemp")
	@CommandPermission("floracore.admin")
	public void dataSetTemp(final @NotNull CommandSender sender,
	                        final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
	                        final @NotNull @Argument("key") String key,
	                        final @NotNull @Argument("value") String value,
	                        final @NotNull @Argument("duration") String duration) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		Player p = Bukkit.getPlayer(target);
		UUID u = uuidCache.getIfPresent(target.toLowerCase());
		if (u == null) {
			if (p == null) {
				try {
					PLAYER i = getStorageImplementation().selectPlayer(target);
					u = i.getUniqueId();
				} catch (Throwable e) {
					MiscMessage.PLAYER_NOT_FOUND.send(s, target);
					return;
				}
			} else {
				u = p.getUniqueId();
			}
			uuidCache.put(target.toLowerCase(), u);
		}
		try {
			Duration d = parseDuration(duration);
			if (d != null) {
				// 将当前时间加上时间差
				Instant newTime = Instant.now().plus(d);
				// 将结果转换为时间戳
				long expiry = newTime.toEpochMilli();
				DATA data = getStorageImplementation().insertData(u, DataType.CUSTOM, key, value, expiry);
				MiscCommandMessage.SET_DATA_TEMP_SUCCESS.send(s, key, value, target, d);
			} else {
				MiscMessage.ILLEGAL_DATE_ERROR.send(s, duration);
			}
		} catch (Throwable e) {
			MiscMessage.PAST_DATE_ERROR.send(s);
		}
	}

	@CommandMethod("fc|floracore data <target> clear")
	@CommandDescription("floracore.command.description.floracore.data.clear")
	@CommandPermission("floracore.admin")
	public void dataClear(final @NotNull CommandSender sender,
	                      final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
	                      final @Nullable @Flag("type") DataType type) {
		Sender s = plugin.getSenderFactory().wrap(sender);
		Player p = Bukkit.getPlayer(target);
		UUID u = uuidCache.getIfPresent(target.toLowerCase());
		if (u == null) {
			if (p == null) {
				try {
					PLAYER i = getStorageImplementation().selectPlayer(target);
					u = i.getUniqueId();
				} catch (Throwable e) {
					MiscMessage.PLAYER_NOT_FOUND.send(s, target);
					return;
				}
			} else {
				u = p.getUniqueId();
			}
			uuidCache.put(target.toLowerCase(), u);
		}
		if (type != null) {
			// remove type
			getStorageImplementation().deleteDataType(u, type);
		} else {
			// remove all
			getStorageImplementation().deleteDataAll(u);
		}
		MiscCommandMessage.DATA_CLEAR_SUCCESS.send(s, target, type);
	}

	@CommandMethod("fc|floracore chat <target>")
	@CommandDescription("floracore.command.description.floracore.chat")
	@CommandPermission("floracore.staff")
	public void chat(final @NotNull CommandSender sender,
	                 final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		getAsyncExecutor().execute(() -> {
			boolean has = getPlugin().getApiProvider().getPlayerAPI().hasPlayerRecord(target);
			if (has) {
				MiscMessage.MISC_GETTING.send(s);
				ChatProvider.Uploader uploader = new ChatProvider.Uploader(s.getUniqueId(), s.getName());
				UUID targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
				ChatProvider chatProvider = new ChatProvider(getPlugin(), uploader, targetUUID);
				try {
					String id = chatProvider.uploadChatData(getPlugin().getBytebin());
					String url = getPlugin().getConfiguration().get(ConfigKeys.CHAT_VIEWER_URL_PATTERN) + id;
					MiscMessage.CHAT_RESULTS_URL.send(s, url);
				} catch (IOException e) {
					getPlugin().getLogger().warn("Error uploading data to bytebin", e);
					MiscMessage.GENERIC_HTTP_UNKNOWN_FAILURE.send(s);
				} catch (UnsuccessfulRequestException e) {
					MiscMessage.GENERIC_HTTP_REQUEST_FAILURE.send(s, e.getResponse().code(),
							e.getResponse().message());
				}
			} else {
				MiscMessage.PLAYER_NOT_FOUND.send(s, target);
			}
		});
	}
}
