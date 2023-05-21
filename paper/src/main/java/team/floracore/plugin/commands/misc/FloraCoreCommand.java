package team.floracore.plugin.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.github.benmanes.caffeine.cache.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;
import team.floracore.common.http.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;
import team.floracore.plugin.*;
import team.floracore.plugin.command.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * FloraCore命令
 */
@CommandContainer
@CommandPermission("floracore.admin")
@CommandDescription("FloraCore插件的主命令")
public class FloraCoreCommand extends AbstractFloraCoreCommand {
    private final AsyncCache<UUID, List<DATA>> dataCache = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).maximumSize(10000).buildAsync();
    private final AsyncCache<String, UUID> uuidCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(10000).buildAsync();

    public FloraCoreCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("fc|floracore reload")
    @CommandDescription("插件配置文件重载")
    public void reload(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        getPlugin().getConfiguration().reload();
        MiscMessage.RELOAD_CONFIG_SUCCESS.send(s);
    }

    @CommandMethod("fc|floracore translations")
    @CommandDescription("插件翻译列表")
    public void translations(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        MiscMessage.TRANSLATIONS_SEARCHING.send(s);

        List<TranslationRepository.LanguageInfo> availableTranslations;
        try {
            availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
        } catch (IOException | UnsuccessfulRequestException e) {
            MiscMessage.TRANSLATIONS_SEARCHING_ERROR.send(s);
            getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
            return;
        }

        MiscMessage.INSTALLED_TRANSLATIONS.send(s, getPlugin().getTranslationManager().getInstalledLocales().stream().map(Locale::toLanguageTag).sorted().collect(Collectors.toList()));

        MiscMessage.AVAILABLE_TRANSLATIONS_HEADER.send(s);
        availableTranslations.stream().sorted(Comparator.comparing(language -> language.locale().toLanguageTag())).forEach(language -> MiscMessage.AVAILABLE_TRANSLATIONS_ENTRY.send(s, language.locale().toLanguageTag(), TranslationManager.localeDisplayName(language.locale()), language.progress(), language.contributors()));
        s.sendMessage(AbstractMessage.prefixed(Component.empty()));
        MiscMessage.TRANSLATIONS_DOWNLOAD_PROMPT.send(s);
    }

    @CommandMethod("fc|floracore translations install")
    @CommandDescription("安装插件翻译列表")
    public void installTranslations(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        MiscMessage.TRANSLATIONS_SEARCHING.send(s);

        List<TranslationRepository.LanguageInfo> availableTranslations;
        try {
            availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
        } catch (IOException | UnsuccessfulRequestException e) {
            MiscMessage.TRANSLATIONS_SEARCHING_ERROR.send(s);
            getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
            return;
        }
        MiscMessage.TRANSLATIONS_INSTALLING.send(s);
        getPlugin().getTranslationRepository().downloadAndInstallTranslations(availableTranslations, s, true);
        MiscMessage.TRANSLATIONS_INSTALL_COMPLETE.send(s);
    }

    @CommandMethod("fc|floracore server <target>")
    @CommandDescription("获取服务器的数据")
    public void server(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "servers") String target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        SERVER server = getStorageImplementation().selectServer(target);
        if (server == null) {
            Message.DATA_NONE.send(s, target);
        } else {
            Component on = Component.translatable("floracore.command.misc.on");
            Component off = Component.translatable("floracore.command.misc.off");
            Message.DATA_HEADER.send(s, target);
            Message.SERVER_DATA_ENTRY.send(s, MiscMessage.COMMAND_SERVER_DATA_TYPE.build(), server.getType().getName());
            Message.SERVER_DATA_ENTRY_1.send(s, MiscMessage.COMMAND_SERVER_DATA_AUTO_SYNC_1.build(), server.isAutoSync1() ? on : off);
            Message.SERVER_DATA_ENTRY_1.send(s, MiscMessage.COMMAND_SERVER_DATA_AUTO_SYNC_2.build(), server.isAutoSync2() ? on : off);
            Message.SERVER_DATA_ENTRY.send(s, MiscMessage.COMMAND_SERVER_DATA_ACTIVE_TIME.build(), DurationFormatter.getTimeFromTimestamp(server.getLastActiveTime()));
        }
    }

    @CommandMethod("fc|floracore server <target> set autosync1 <value>")
    @CommandDescription("设置服务器Sync1的数据")
    public void serverSet1(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "servers") String target, final @Argument("value") boolean value) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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
    @CommandDescription("设置服务器Sync2的数据")
    public void serverSet2(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "servers") String target, final @Argument("value") boolean value) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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
        return new ArrayList<>(Collections.singletonList(getPlugin().getServerName()));
    }

    @CommandMethod("fc|floracore data <target> <type>")
    @CommandDescription("获取玩家存储的数据")
    public void data(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, @Argument("type") DataType type) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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
            Message.DATA_NONE.send(s, target);
        } else {
            Message.DATA_HEADER.send(s, target);
            for (DATA data : ret) {
                Message.DATA_ENTRY.send(s, data.getType().getName(), data.getKey(), data.getValue(), data.getExpiry());
            }
        }
    }

    @CommandMethod("fc|floracore data <target> set <key> <value>")
    @CommandDescription("设置玩家自定义的数据")
    public void dataSet(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NonNull @Argument("key") String key, final @NonNull @Argument("value") String value) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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
        Message.SET_DATA_SUCCESS.send(s, key, value, target);
    }

    @CommandMethod("fc|floracore data <target> unset <key>")
    @CommandDescription("删除玩家存储的自定义数据")
    public void dataUnSet(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NonNull @Argument("key") String key) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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
            Message.DOESNT_HAVE_DATA.send(s, target, key);
            return;
        }
        getStorageImplementation().deleteDataID(data.getId());
        Message.UNSET_DATA_SUCCESS.send(s, key, target);
    }

    @CommandMethod("fc|floracore data <target> settemp <key> <value> <duration>")
    @CommandDescription("临时设置玩家自定义的数据")
    public void dataSetTemp(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NonNull @Argument("key") String key, final @NonNull @Argument("value") String value, final @NonNull @Argument("duration") String duration) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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
                Message.SET_DATA_TEMP_SUCCESS.send(s, key, value, target, d);
            } else {
                MiscMessage.ILLEGAL_DATE_ERROR.send(s, duration);
            }
        } catch (Throwable e) {
            MiscMessage.PAST_DATE_ERROR.send(s);
        }
    }

    @CommandMethod("fc|floracore data <target> clear")
    @CommandDescription("清空玩家存储的数据")
    public void dataClear(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @Nullable @Flag("type") DataType type) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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
        Message.DATA_CLEAR_SUCCESS.send(s, target, type);
    }
}
