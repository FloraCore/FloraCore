package team.floracore.common.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.Nullable;
import team.floracore.api.data.*;
import team.floracore.common.command.*;
import team.floracore.common.http.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

@CommandContainer
@CommandPermission("floracore.admin")
public class FloraCoreCommand extends AbstractFloraCoreCommand {
    public FloraCoreCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("fc|floracore reload")
    @CommandDescription("插件配置文件重载")
    public void reload(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        getPlugin().getConfiguration().reload();
        Message.RELOAD_CONFIG_SUCCESS.send(s);
    }

    @CommandMethod("fc|floracore translations")
    @CommandDescription("插件翻译列表")
    public void translations(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Message.TRANSLATIONS_SEARCHING.send(s);

        List<TranslationRepository.LanguageInfo> availableTranslations;
        try {
            availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
        } catch (IOException | UnsuccessfulRequestException e) {
            Message.TRANSLATIONS_SEARCHING_ERROR.send(s);
            getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
            return;
        }

        Message.INSTALLED_TRANSLATIONS.send(s, getPlugin().getTranslationManager().getInstalledLocales().stream().map(Locale::toLanguageTag).sorted().collect(Collectors.toList()));

        Message.AVAILABLE_TRANSLATIONS_HEADER.send(s);
        availableTranslations.stream().sorted(Comparator.comparing(language -> language.locale().toLanguageTag())).forEach(language -> Message.AVAILABLE_TRANSLATIONS_ENTRY.send(s, language.locale().toLanguageTag(), TranslationManager.localeDisplayName(language.locale()), language.progress(), language.contributors()));
        s.sendMessage(Message.prefixed(Component.empty()));
        Message.TRANSLATIONS_DOWNLOAD_PROMPT.send(s);
    }

    @CommandMethod("fc|floracore translations install")
    @CommandDescription("插件翻译列表")
    public void installTranslations(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Message.TRANSLATIONS_SEARCHING.send(s);

        List<TranslationRepository.LanguageInfo> availableTranslations;
        try {
            availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
        } catch (IOException | UnsuccessfulRequestException e) {
            Message.TRANSLATIONS_SEARCHING_ERROR.send(s);
            getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
            return;
        }
        Message.TRANSLATIONS_INSTALLING.send(s);
        getPlugin().getTranslationRepository().downloadAndInstallTranslations(availableTranslations, s, true);
        Message.TRANSLATIONS_INSTALL_COMPLETE.send(s);
    }

    @CommandMethod("fc|floracore server <target>")
    @CommandDescription("获取服务器的数据")
    public void server(final @NonNull CommandSender sender, final @NonNull @Argument("target") String target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        Servers servers = storageImplementation.selectServers(target);
        if (servers == null) {
            // TODO 无记录的服务器数据
            System.out.println(false);
        } else {
            // TODO 返回服务器的数据
            System.out.println(true);
        }
    }

    @CommandMethod("fc|floracore server <target> set autosync <value>")
    @CommandDescription("设置服务器的数据")
    public void serverSet(final @NonNull CommandSender sender, final @NonNull @Argument("target") String target, final @Argument("value") boolean value) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        Servers servers = storageImplementation.selectServers(target);
        if (servers == null) {
            // TODO 无记录的服务器数据
            System.out.println(false);
        } else {
            // TODO 设置服务器的数据
            servers.setAutoSync(value);
            System.out.println(true);
        }
    }

    @CommandMethod("fc|floracore data <target>")
    @CommandDescription("获取玩家存储的数据")
    public void data(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        String name;
        UUID u;
        if (p == null) {
            try {
                Players i = storageImplementation.selectPlayers(target);
                u = i.getUuid();
                name = i.getName();
            } catch (Throwable e) {
                Message.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
        } else {
            u = p.getUniqueId();
            name = p.getName();
        }
        List<Data> ret = storageImplementation.selectData(u);
        if (ret.isEmpty()) {
            Message.DATA_NONE.send(s, name);
        } else {
            Message.DATA_HEADER.send(s, name);
            for (Data data : ret) {
                Message.DATA_ENTRY.send(s, data.getType().getName(), data.getKey(), data.getValue(), data.getExpiry());
            }
        }
    }

    @CommandMethod("fc|floracore data <target> set <key> <value>")
    @CommandDescription("设置玩家自定义的数据")
    public void dataSet(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NonNull @Argument("key") String key, final @NonNull @Argument("value") String value) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        String name;
        UUID u;
        if (p == null) {
            try {
                Players i = storageImplementation.selectPlayers(target);
                u = i.getUuid();
                name = i.getName();
            } catch (Throwable e) {
                Message.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
        } else {
            u = p.getUniqueId();
            name = p.getName();
        }
        Data data = storageImplementation.insertData(u, DataType.CUSTOM, key, value, 0);
        Message.SET_DATA_SUCCESS.send(s, key, value, name);
    }

    @CommandMethod("fc|floracore data <target> unset <key>")
    @CommandDescription("删除玩家存储的自定义数据")
    public void dataUnSet(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NonNull @Argument("key") String key) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        String name;
        UUID u;
        if (p == null) {
            try {
                Players i = storageImplementation.selectPlayers(target);
                u = i.getUuid();
                name = i.getName();
            } catch (Throwable e) {
                Message.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
        } else {
            u = p.getUniqueId();
            name = p.getName();
        }
        Data data = storageImplementation.getSpecifiedData(u, DataType.CUSTOM, key);
        if (data == null) {
            Message.DOESNT_HAVE_DATA.send(s, target, key);
            return;
        }
        storageImplementation.deleteDataID(data.getId());
        Message.UNSET_DATA_SUCCESS.send(s, key, name);
    }

    @CommandMethod("fc|floracore data <target> settemp <key> <value> <duration>")
    @CommandDescription("临时设置玩家自定义的数据")
    public void dataSetTemp(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NonNull @Argument("key") String key, final @NonNull @Argument("value") String value, final @NonNull @Argument("duration") String duration) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        String name;
        UUID u;
        if (p == null) {
            try {
                Players i = storageImplementation.selectPlayers(target);
                u = i.getUuid();
                name = i.getName();
            } catch (Throwable e) {
                Message.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
        } else {
            u = p.getUniqueId();
            name = p.getName();
        }

        try {
            Duration d = parseDuration(duration);
            if (d != null) {
                // 将当前时间加上时间差
                Instant newTime = Instant.now().plus(d);
                // 将结果转换为时间戳
                long expiry = newTime.toEpochMilli();
                Data data = storageImplementation.insertData(u, DataType.CUSTOM, key, value, expiry);
                Message.SET_DATA_TEMP_SUCCESS.send(s, key, value, name, d);
            } else {
                Message.ILLEGAL_DATE_ERROR.send(s, duration);
            }
        } catch (Throwable e) {
            Message.PAST_DATE_ERROR.send(s);
        }
    }

    @CommandMethod("fc|floracore data <target> clear")
    @CommandDescription("清空玩家存储的数据")
    public void dataClear(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @Nullable @Flag("type") DataType type) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player p = Bukkit.getPlayer(target);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        String name;
        UUID u;
        if (p == null) {
            try {
                Players i = storageImplementation.selectPlayers(target);
                u = i.getUuid();
                name = i.getName();
            } catch (Throwable e) {
                Message.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
        } else {
            u = p.getUniqueId();
            name = p.getName();
        }
        if (type != null) {
            // remove type
            storageImplementation.deleteDataType(u, type);
        } else {
            // remove all
            storageImplementation.deleteDataAll(u);
        }
        Message.DATA_CLEAR_SUCCESS.send(s, name, type);
    }
}
