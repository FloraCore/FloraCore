package team.floracore.common.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.command.*;
import team.floracore.common.http.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.io.*;
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

    @CommandMethod("fc|floracore data <target>")
    @CommandDescription("获取玩家存储的数据")
    public void data(final @NonNull CommandSender sender, final @Argument(value = "target", suggestions = "onlinePlayers") String target) {
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
                Message.DATA_ENTRY.send(s, data.getType(), data.getKey(), data.getValue(), data.getExpiry());
            }
        }
    }

}
