package team.floracore.bungee.commands.misc;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;
import team.floracore.bungee.locale.message.commands.MiscCommandMessage;
import team.floracore.common.http.UnsuccessfulRequestException;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.CommonCommandMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.locale.translation.TranslationRepository;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.SERVER;
import team.floracore.common.util.DurationFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
	public void reload(final @NotNull CommandSender sender) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		getPlugin().getConfiguration().reload();
		getPlugin().getChatConfiguration().reload();
		getPlugin().getTranslationManager().reload();
		CommonCommandMessage.RELOAD_CONFIG_SUCCESS.send(s);
	}

	@CommandMethod("fcb|floracorebungee translations")
	@CommandDescription("floracore.command.description.floracore.translations")
	public void translations(final @NotNull CommandSender sender) {
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
				                     language.locale()
				                             .toLanguageTag(),
				                     TranslationManager.localeDisplayName(
						                     language.locale()),
				                     language.progress(),
				                     language.contributors()));
		s.sendMessage(AbstractMessage.prefixed(Component.empty()));
		CommonCommandMessage.TRANSLATIONS_DOWNLOAD_PROMPT.send(s);
	}

	@CommandMethod("fcb|floracorebungee translations install")
	@CommandDescription("floracore.command.description.floracore.translations.install")
	public void installTranslations(final @NotNull CommandSender sender) {
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
	public void server(final @NotNull CommandSender sender,
	                   final @NotNull @Argument(value = "target", suggestions = "servers") String target) {
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
	public void serverSet1(final @NotNull CommandSender sender,
	                       final @NotNull @Argument(value = "target", suggestions = "servers") String target,
	                       final @Argument("value") boolean value) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		SERVER server = getStorageImplementation().selectServer(target);
		if (server == null) {
			MiscMessage.SERVER_NOT_FOUND.send(s, target);
		} else {
			server.setAutoSync1(value);
			MiscCommandMessage.SET_SERVER_DATA.send(s, target, MiscMessage.COMMAND_SERVER_DATA_AUTO_SYNC_1.build(),
					value);
		}
	}

	@CommandMethod("fcb|floracorebungee server <target> set autosync2 <value>")
	@CommandDescription("floracore.command.description.floracore.server.set.autosync2")
	public void serverSet2(final @NotNull CommandSender sender,
	                       final @NotNull @Argument(value = "target", suggestions = "servers") String target,
	                       final @Argument("value") boolean value) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		SERVER server = getStorageImplementation().selectServer(target);
		if (server == null) {
			MiscMessage.SERVER_NOT_FOUND.send(s, target);
		} else {
			server.setAutoSync2(value);
			MiscCommandMessage.SET_SERVER_DATA.send(s, target, MiscMessage.COMMAND_SERVER_DATA_AUTO_SYNC_2.build(),
					value);
		}
	}

	@Suggestions("servers")
	public List<String> getServers(final @NotNull CommandContext<CommandSender> sender, final @NotNull String input) {
		return new ArrayList<>(getPlugin().getProxy().getServersCopy().keySet());
	}
}
