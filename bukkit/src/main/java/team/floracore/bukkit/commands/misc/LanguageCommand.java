package team.floracore.bukkit.commands.misc;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickCancelUpdateAllInterface;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.floracore.api.data.DataType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.MenuMessage;
import team.floracore.bukkit.locale.message.commands.MiscCommandMessage;
import team.floracore.bukkit.util.itemstack.ItemStackBuilder;
import team.floracore.common.http.UnsuccessfulRequestException;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.locale.translation.TranslationRepository;
import team.floracore.common.sender.Sender;

import java.io.IOException;
import java.util.*;

@CommandContainer
@CommandDescription("floracore.command.description.language")
public class LanguageCommand extends FloraCoreBukkitCommand {
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
		List<TranslationRepository.LanguageInfo> availableTranslations = new ArrayList<>();
		try {
			availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
		} catch (IOException | UnsuccessfulRequestException e) {
			getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
			return null;
		}
		List<TranslationRepository.LanguageInfo> finalAvailableTranslations = availableTranslations;
		Locale defaultLanguage = TranslationManager.DEFAULT_LOCALE;
		String dpl = TranslationManager.localeDisplayName(defaultLanguage);
		Component dc = TranslationManager.render(MenuMessage.COMMAND_LANGUAGE_CHANGE.build(dpl));
		ItemStackBuilder dib = new ItemStackBuilder(Material.PAPER).setName(Component.text(dpl)
		                                                                             .color(NamedTextColor.GREEN))
		                                                           .setLore(Collections.singletonList(dc));
		Button db = Button.of(p -> dib.get(), p -> {
			{
				String value = defaultLanguage.toLanguageTag();
				getStorageImplementation().insertData(uuid, DataType.FUNCTION, "language", value.replace("-", "_"), 0);
				player.closeInventory();
				MiscCommandMessage.COMMAND_LANGUAGE_CHANGE_SUCCESS.send(s, dpl);
			}
		});
		buttons.add(db);
		for (TranslationRepository.LanguageInfo language : finalAvailableTranslations) {
			Locale l = language.locale();
			String pl = TranslationManager.localeDisplayName(l);
			Component c = TranslationManager.render(MenuMessage.COMMAND_LANGUAGE_CHANGE.build(pl), l);
			Component t = Component.text(pl).color(NamedTextColor.GREEN);
			Component progress = AbstractMessage.OPEN_BRACKET.append(Component.text(language.progress() + "%"))
			                                                 .append(AbstractMessage.CLOSE_BRACKET);
			t = t.append(Component.space()).append(progress.color(NamedTextColor.GRAY));
			ItemStackBuilder itemBuilder = new ItemStackBuilder(Material.PAPER).setName(t)
			                                                                   .setLore(Collections.singletonList(c));
			Button b = Button.of(p -> itemBuilder.get(), p -> {
				{
					String value = language.locale().toLanguageTag();
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
		gui.addAttachedButton(
				new GuiButton(
						Slot.ofGame(1, 6),
						Button.of(
								p -> {
									if (gui.pagination().hasLast(gui.page())) {
										Component previous =
												TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(),
												uuid);
										Component turn =
												TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(gui.page() - 1),
												uuid);
										return new ItemStackBuilder(Material.ARROW).setName(previous)
										                                           .setLore(Collections.singletonList(
												                                           turn))
										                                           .get();
									} else {
										return new ItemStack(Material.AIR);
									}
								},
								/*点击后刷新所有按钮*/
								(PlayerClickCancelUpdateAllInterface) (p, click, action, slotType, slot, hotBarKey) -> {
									if (gui.pagination().hasLast(gui.page())) {
										gui.page(gui.page() - 1);
									}
								}
						         )
				)
		                     );
		gui.addAttachedButton(
				new GuiButton(
						Slot.ofGame(9, 6),
						Button.of(
								p -> {
									if (gui.pagination().hasNext(gui.page())) {
										Component next =
												TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
										Component turn =
												TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(gui.page() + 1),
												uuid);
										return new ItemStackBuilder(Material.ARROW).setName(next)
										                                           .setLore(Collections.singletonList(
												                                           turn))
										                                           .get();
									} else {
										return new ItemStack(Material.AIR);
									}
								},
								/*点击后刷新所有按钮*/
								(PlayerClickCancelUpdateAllInterface) (p, click, action, slotType, slot, hotBarKey) -> {
									if (gui.pagination().hasNext(gui.page())) {
										gui.page(gui.page() + 1);
									}
								}
						         )
				)
		                     );
		return gui;
	}
}
