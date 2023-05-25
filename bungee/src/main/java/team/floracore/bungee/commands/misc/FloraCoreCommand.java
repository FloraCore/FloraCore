package team.floracore.bungee.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import net.kyori.adventure.text.*;
import net.md_5.bungee.api.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;
import team.floracore.bungee.*;
import team.floracore.bungee.command.*;
import team.floracore.common.http.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * FloraCore命令
 */
@CommandContainer
@CommandPermission("floracore.admin")
@CommandDescription("floracore.command.description.floracore")
public class FloraCoreCommand extends FloraCoreBungeeCommand {
    public FloraCoreCommand(FCBungeePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("fcb|floracorebungee reload")
    @CommandDescription("floracore.command.description.floracore.reload")
    public void reload(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        getPlugin().getConfiguration().reload();
        CommonCommandMessage.RELOAD_CONFIG_SUCCESS.send(s);
    }

    @CommandMethod("fcb|floracorebungee translations")
    @CommandDescription("floracore.command.description.floracore.translations")
    public void translations(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        CommonCommandMessage.TRANSLATIONS_SEARCHING.send(s);

        List<TranslationRepository.LanguageInfo> availableTranslations;
        try {
            availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
        } catch (IOException | UnsuccessfulRequestException e) {
            CommonCommandMessage.TRANSLATIONS_SEARCHING_ERROR.send(s);
            getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
            return;
        }

        CommonCommandMessage.INSTALLED_TRANSLATIONS.send(s,
                getPlugin().getTranslationManager()
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

    @CommandMethod("fcb|floracorebungee translations install")
    @CommandDescription("floracore.command.description.floracore.translations.install")
    public void installTranslations(final @NonNull CommandSender sender) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        CommonCommandMessage.TRANSLATIONS_SEARCHING.send(s);

        List<TranslationRepository.LanguageInfo> availableTranslations;
        try {
            availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
        } catch (IOException | UnsuccessfulRequestException e) {
            CommonCommandMessage.TRANSLATIONS_SEARCHING_ERROR.send(s);
            getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
            return;
        }
        CommonCommandMessage.TRANSLATIONS_INSTALLING.send(s);
        getPlugin().getTranslationRepository().downloadAndInstallTranslations(availableTranslations, s, true);
        CommonCommandMessage.TRANSLATIONS_INSTALL_COMPLETE.send(s);
    }

    @CommandMethod("fcb|floracorebungee server <target>")
    @CommandDescription("floracore.command.description.floracore.server")
    public void server(final @NonNull CommandSender sender,
                       final @NonNull @Argument(value = "target", suggestions = "servers") String target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
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

    @CommandMethod("fcb|floracorebungee server <target> set autosync1 <value>")
    @CommandDescription("floracore.command.description.floracore.server.set.autosync1")
    public void serverSet1(final @NonNull CommandSender sender,
                           final @NonNull @Argument(value = "target", suggestions = "servers") String target,
                           final @Argument("value") boolean value) {
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

    @CommandMethod("fcb|floracorebungee server <target> set autosync2 <value>")
    @CommandDescription("floracore.command.description.floracore.server.set.autosync2")
    public void serverSet2(final @NonNull CommandSender sender,
                           final @NonNull @Argument(value = "target", suggestions = "servers") String target,
                           final @Argument("value") boolean value) {
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
        return new ArrayList<>(getPlugin().getBootstrap().getProxy().getServersCopy().keySet());
    }
}
