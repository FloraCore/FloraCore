package team.floracore.bukkit.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.floracore.api.data.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.MiscCommandMessage;
import team.floracore.common.config.ConfigKeys;
import team.floracore.common.http.UnsuccessfulRequestException;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.CommonCommandMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.locale.translation.TranslationRepository;
import team.floracore.common.messaging.InternalMessagingService;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;
import team.floracore.common.storage.misc.floracore.tables.PLAYER;
import team.floracore.common.storage.misc.floracore.tables.SERVER;
import team.floracore.common.util.DurationFormatter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * FloraCore命令
 */
@CommandContainer
@CommandPermission("floracore.admin")
@CommandDescription("floracore.command.description.floracore")
public class FloraCoreCommand extends FloraCoreBukkitCommand {
    private final FCBukkitPlugin plugin;
    private final AsyncCache<UUID, List<DATA>> dataCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .maximumSize(10000)
            .buildAsync();
    private final AsyncCache<String, UUID> uuidCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(10000)
            .buildAsync();

    public FloraCoreCommand(FCBukkitPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @CommandMethod("fc|floracore reload")
    @CommandDescription("floracore.command.description.floracore.reload")
    public void reload(final @NotNull CommandSender sender) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        plugin.getConfiguration().reload();
        // close messaging service
        if (plugin.getMessagingService() != null) {
            plugin.getLogger().info("Closing messaging service...");
            plugin.getMessagingService().ifPresent(InternalMessagingService::close);
        }
        // 重载数据库服务
        plugin.getLogger().info("Closing storage...");
        plugin.getStorage().shutdown();
        plugin.getLogger().info("Reloading storage and messaging service...");
        plugin.getDependencyManager().loadStorageDependencies(plugin.getStorageFactory().getRequiredTypes(),
                plugin.getConfiguration().get(ConfigKeys.REDIS_ENABLED));
        // initialise storage
        plugin.setStorage(plugin.getStorageFactory().getInstance());
        plugin.setMessagingService(plugin.provideMessagingFactory().getInstance());
        plugin.getBoardsConfiguration().reload();
        plugin.getTranslationManager().reload();
        plugin.getScoreBoardManager().reload();
        CommonCommandMessage.RELOAD_CONFIG_SUCCESS.send(s);
    }

    @CommandMethod("fc|floracore translations")
    @CommandDescription("floracore.command.description.floracore.translations")
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
                        language.locale().toLanguageTag(),
                        TranslationManager.localeDisplayName(language.locale()),
                        language.progress(),
                        language.contributors()));
        s.sendMessage(AbstractMessage.prefixed(Component.empty()));
        CommonCommandMessage.TRANSLATIONS_DOWNLOAD_PROMPT.send(s);
    }

    @CommandMethod("fc|floracore translations install")
    @CommandDescription("floracore.command.description.floracore.translations.install")
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

    @CommandMethod("fc|floracore server <target>")
    @CommandDescription("floracore.command.description.floracore.server")
    public void server(final @NotNull CommandSender sender,
                       final @NotNull @Argument(value = "target", suggestions = "servers") String target) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        SERVER server = getStorageImplementation().selectServer(target);
        if (server == null) {
            CommonCommandMessage.DATA_NONE.send(s, target);
        } else {
            Component on = Component.translatable("floracore.command.misc.on");
            Component off = Component.translatable("floracore.command.misc.off");
            CommonCommandMessage.DATA_HEADER.send(s, target);
            CommonCommandMessage.SERVER_DATA_ENTRY.send(s,
                    MiscMessage.COMMAND_SERVER_DATA_TYPE.build(),
                    server.getType().getName());
            CommonCommandMessage.SERVER_DATA_ENTRY_1.send(s,
                    MiscMessage.COMMAND_SERVER_DATA_AUTO_SYNC_1.build(),
                    server.isAutoSync1() ? on : off);
            CommonCommandMessage.SERVER_DATA_ENTRY_1.send(s,
                    MiscMessage.COMMAND_SERVER_DATA_AUTO_SYNC_2.build(),
                    server.isAutoSync2() ? on : off);
            CommonCommandMessage.SERVER_DATA_ENTRY.send(s,
                    MiscMessage.COMMAND_SERVER_DATA_ACTIVE_TIME.build(),
                    DurationFormatter.getTimeFromTimestamp(server.getLastActiveTime()));
        }
    }

    @CommandMethod("fc|floracore server <target> set autosync1 <value>")
    @CommandDescription("floracore.command.description.floracore.server.set.autosync1")
    public void serverSet1(final @NotNull CommandSender sender,
                           final @NotNull @Argument(value = "target", suggestions = "servers") String target,
                           final @Argument("value") boolean value) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        SERVER server = getStorageImplementation().selectServer(target);
        if (server == null) {
            // TODO 无记录的服务器数据
            System.out.println(false);
        } else {
            // TODO 设置服务器的数据
            server.setAutoSync1(value);
            System.out.println(true);
        }
    }

    @CommandMethod("fc|floracore server <target> set autosync2 <value>")
    @CommandDescription("floracore.command.description.floracore.server.set.autosync2")
    public void serverSet2(final @NotNull CommandSender sender,
                           final @NotNull @Argument(value = "target", suggestions = "servers") String target,
                           final @Argument("value") boolean value) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        SERVER server = getStorageImplementation().selectServer(target);
        if (server == null) {
            // TODO 无记录的服务器数据
            System.out.println(false);
        } else {
            // TODO 设置服务器的数据
            server.setAutoSync2(value);
            System.out.println(true);
        }
    }

    @Suggestions("servers")
    public List<String> getServers(final @NotNull CommandContext<CommandSender> sender, final @NotNull String input) {
        return new ArrayList<>(Collections.singletonList(plugin.getServerName()));
    }

    @CommandMethod("fc|floracore data <target> <type>")
    @CommandDescription("floracore.command.description.floracore.data")
    public void data(final @NotNull CommandSender sender,
                     final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                     @Argument("type") DataType type) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        CompletableFuture<UUID> uf = uuidCache.get(target.toLowerCase(), (a) -> {
            UUID u;
            if (p == null) {
                try {
                    PLAYER i = getStorageImplementation().selectPlayer(target);
                    u = i.getUniqueId();
                } catch (Throwable e) {
                    MiscMessage.PLAYER_NOT_FOUND.send(s, target);
                    return null;
                }
            } else {
                u = p.getUniqueId();
            }
            return u;
        });
        uuidCache.put(target.toLowerCase(), uf);
        UUID u = uf.join();
        if (u == null) {
            return;
        }
        CompletableFuture<List<DATA>> ldf = dataCache.get(u, (a) -> getStorageImplementation().selectData(u));
        List<DATA> all = ldf.join();
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
    public void dataSet(final @NotNull CommandSender sender,
                        final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                        final @NotNull @Argument("key") String key,
                        final @NotNull @Argument("value") String value) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        CompletableFuture<UUID> uf = uuidCache.get(target.toLowerCase(), (a) -> {
            UUID u;
            if (p == null) {
                try {
                    PLAYER i = getStorageImplementation().selectPlayer(target);
                    u = i.getUniqueId();
                } catch (Throwable e) {
                    MiscMessage.PLAYER_NOT_FOUND.send(s, target);
                    return null;
                }
            } else {
                u = p.getUniqueId();
            }
            return u;
        });
        uuidCache.put(target.toLowerCase(), uf);
        UUID u = uf.join();
        if (u == null) {
            return;
        }
        DATA data = getStorageImplementation().insertData(u, DataType.CUSTOM, key, value, 0);
        MiscCommandMessage.SET_DATA_SUCCESS.send(s, key, value, target);
    }

    @CommandMethod("fc|floracore data <target> unset <key>")
    @CommandDescription("floracore.command.description.floracore.data.unset")
    public void dataUnSet(final @NotNull CommandSender sender,
                          final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                          final @NotNull @Argument("key") String key) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        CompletableFuture<UUID> uf = uuidCache.get(target.toLowerCase(), (a) -> {
            UUID u;
            if (p == null) {
                try {
                    PLAYER i = getStorageImplementation().selectPlayer(target);
                    u = i.getUniqueId();
                } catch (Throwable e) {
                    MiscMessage.PLAYER_NOT_FOUND.send(s, target);
                    return null;
                }
            } else {
                u = p.getUniqueId();
            }
            return u;
        });
        uuidCache.put(target.toLowerCase(), uf);
        UUID u = uf.join();
        if (u == null) {
            return;
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
    public void dataSetTemp(final @NotNull CommandSender sender,
                            final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                            final @NotNull @Argument("key") String key,
                            final @NotNull @Argument("value") String value,
                            final @NotNull @Argument("duration") String duration) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        CompletableFuture<UUID> uf = uuidCache.get(target.toLowerCase(), (a) -> {
            UUID u;
            if (p == null) {
                try {
                    PLAYER i = getStorageImplementation().selectPlayer(target);
                    u = i.getUniqueId();
                } catch (Throwable e) {
                    MiscMessage.PLAYER_NOT_FOUND.send(s, target);
                    return null;
                }
            } else {
                u = p.getUniqueId();
            }
            return u;
        });
        uuidCache.put(target.toLowerCase(), uf);
        UUID u = uf.join();
        if (u == null) {
            return;
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
    public void dataClear(final @NotNull CommandSender sender,
                          final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                          final @Nullable @Flag("type") DataType type) {
        Sender s = plugin.getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        CompletableFuture<UUID> uf = uuidCache.get(target.toLowerCase(), (a) -> {
            UUID u;
            if (p == null) {
                try {
                    PLAYER i = getStorageImplementation().selectPlayer(target);
                    u = i.getUniqueId();
                } catch (Throwable e) {
                    MiscMessage.PLAYER_NOT_FOUND.send(s, target);
                    return null;
                }
            } else {
                u = p.getUniqueId();
            }
            return u;
        });
        uuidCache.put(target.toLowerCase(), uf);
        UUID u = uf.join();
        if (u == null) {
            return;
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
}
