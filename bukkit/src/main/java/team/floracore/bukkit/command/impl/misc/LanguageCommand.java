package team.floracore.bukkit.command.impl.misc;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.github.benmanes.caffeine.cache.Cache;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.floracore.api.model.data.DataType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.MenuMessage;
import team.floracore.bukkit.locale.message.commands.MiscCommandMessage;
import team.floracore.bukkit.util.GuiUtil;
import team.floracore.bukkit.util.itemstack.ItemStackBuilder;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.sender.Sender;
import team.floracore.common.util.CaffeineFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandContainer
@CommandDescription("floracore.command.description.language")
public class LanguageCommand extends FloraCoreBukkitCommand {
	private static final Cache<Integer, Set<Locale>> languageCache = CaffeineFactory.newBuilder()
			.expireAfterWrite(30,
					TimeUnit.MINUTES)
			.build();

	public LanguageCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("language|lang")
	public void language(final @NotNull Player player) {
		Sender s = getPlugin().getSenderFactory().wrap(player);
		GuiPage guiPage = getLanguageGui(player);
		if (guiPage == null) {
			MiscMessage.CHECK_CONSOLE_FOR_ERRORS.send(s);
			return;
		}
		guiPage.openGui();
	}

	public GuiPage getLanguageGui(Player player) {
		Sender s = getPlugin().getSenderFactory().wrap(player);
		UUID uuid = player.getUniqueId();
		List<Button> buttons = new ArrayList<>();
		Set<Locale> availableTranslations = languageCache.getIfPresent(0);
		if (availableTranslations == null) {
			availableTranslations = getPlugin().getTranslationManager().getInstalledLocales();
			if (!availableTranslations.isEmpty()) {
				languageCache.put(0, availableTranslations);
			}
		}
		Set<Locale> finalAvailableTranslations = availableTranslations;
		for (Locale languageLocale : finalAvailableTranslations) {
			if (!languageLocale.toLanguageTag().contains("-")) {
				continue;
			}
			String pl = TranslationManager.localeDisplayName(languageLocale);
			Button b = Button.of(p -> {
				Component c = TranslationManager.render(MenuMessage.COMMAND_LANGUAGE_CHANGE.build(pl), languageLocale);
				Component t = Component.text(pl).color(NamedTextColor.GREEN);
				ItemStackBuilder itemBuilder = new ItemStackBuilder(Material.PAPER).setName(t)
						.setLore(Collections.singletonList(c));
				return itemBuilder.get();
			}, p -> {
				{
					String value = languageLocale.toLanguageTag();
					getStorageImplementation().insertData(uuid,
							DataType.FUNCTION,
							"language",
							value.replace("-", "_"),
							0);
					player.closeInventory();
					MiscCommandMessage.COMMAND_LANGUAGE_CHANGE_SUCCESS.send(s, pl);
				}
			});
			buttons.add(b);
		}
		Component title = TranslationManager.render(MenuMessage.COMMAND_LANGUAGE_TITLE.build(), uuid);
		Slots LINE = Slots.pattern(new String[]{
				"---------",
				"-x-x-x-x-",
				"-x-x-x-x-",
				"-x-x-x-x-",
				"-x-x-x-x-",
				"---------"
		}, 'x');
		GuiPage gui = new GuiPage(player, buttons, 16, LINE);
		gui.title(title);
		gui.setPlayer(player);
		GuiUtil.createPageButtons(uuid, gui);
		return gui;
	}
}
