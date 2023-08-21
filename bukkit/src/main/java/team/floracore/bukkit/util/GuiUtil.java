package team.floracore.bukkit.util;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSettings;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import team.floracore.bukkit.util.itemstack.ItemStackBuilder;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;

import java.util.Collections;
import java.util.UUID;

public class GuiUtil {
	public static void createPageButtons(UUID uuid, GuiPage gui) {
		Button bp = Button.of(p -> {
			Component previous =
					TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(), uuid);
			Component turn =
					TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(gui.page() - 1), uuid);
			return new ItemStackBuilder(Material.ARROW).setName(previous)
					.setLore(Collections.singletonList(turn))
					.get();
		});
		Button bn = Button.of(p -> {
			Component next =
					TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
			Component turn1 =
					TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(gui.page() + 1),
							uuid);
			return new ItemStackBuilder(Material.ARROW).setName(next)
					.setLore(Collections.singletonList(turn1))
					.get();
		});
		gui.pageSetting(PageSettings.normal(gui, bp, bn));
	}
}
