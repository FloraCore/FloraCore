package team.floracore.bukkit.locale.message;

import team.floracore.common.locale.message.AbstractMessage;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public interface MenuMessage extends AbstractMessage {
	Args0 COMMAND_MISC_GUI_CLOSE = () -> translatable()
			// 关闭
			.key("floracore.command.misc.gui.close").color(RED).build();

	Args0 COMMAND_LANGUAGE_TITLE = () -> translatable()
			// 切换你的显示语言
			.key("floracore.command.misc.language.title").color(BLACK).build();

	Args1<String> COMMAND_LANGUAGE_CHANGE = (language) -> translatable()
			// 点击切换为 {0} !
			.key("floracore.command.misc.language.change")
			.args(text(language).decoration(BOLD, true))
			.color(YELLOW)
			.build();

}
