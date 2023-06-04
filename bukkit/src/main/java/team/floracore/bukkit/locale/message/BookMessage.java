package team.floracore.bukkit.locale.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import team.floracore.bukkit.commands.player.NickCommand;
import team.floracore.common.locale.message.AbstractMessage;

import java.util.UUID;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

public interface BookMessage extends AbstractMessage {
	Args0 COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_LINE_1 = () -> translatable()
			// 我们已经为你生成了一个随机的昵称:
			.key("floracore.command.misc.nick.book.random-page.line.1").color(BLACK).build();

	Args1<String> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_NAME = (name) -> translatable()
			// {0}
			.key("floracore.command.misc.nick.book.random-page.name")
			.args(text(name).decoration(BOLD, true))
			.color(BLACK)
			.build();

	Args3<String, NickCommand.SkinType, String> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_USE_NAME = (rank, skin, name) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 5 " + rank + " " + skin.name() + " random " + name);
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击以使用这个昵称
				.key("floracore.command.misc.nick.book.random-page.use-name.hover")
				.color(WHITE)
				.build());
		return space().append(space()).append(space()).append(translatable()
				// 使用这个昵称
				.key("floracore.command.misc.nick.book.random-page.use-name")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.decoration(UNDERLINED, true)
				.color(GREEN)
				.build());
	};

	Args2<String, NickCommand.SkinType> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_TRY_AGAIN = (rank, skin) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin.name() + " random");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里生成另一个昵称
				.key("floracore.command.misc.nick.book.random-page.try-again.hover")
				.color(WHITE)
				.build());
		return space().append(space()).append(space()).append(translatable()
				// 重新生成
				.key("floracore.command.misc.nick.book.random-page.try-again")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.decoration(UNDERLINED, true)
				.color(RED)
				.build());
	};

	Args2<String, NickCommand.SkinType> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_CUSTOM = (rank, skin) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin.name() + " custom");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里以输入自定义昵称
				.key("floracore.command.misc.nick.book.random-page.custom.hover")
				.color(WHITE)
				.build());
		return translatable()
				// 或者使用自定义昵称
				.key("floracore.command.misc.nick.book.random-page.custom")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.decoration(UNDERLINED, true)
				.color(BLACK)
				.build();
	};

	Args0 COMMAND_MISC_NICK_BOOK_NAME_PAGE_LINE_1 = () -> translatable()
			// 现在,请选择你要使用的{0}!
			.key("floracore.command.misc.nick.book.name-page.line.1")
			// {0}
			.args(translatable("floracore.command.misc.nick.book.name-page.name").decoration(BOLD, true))
			.color(BLACK)
			.build();

	Args0 COMMAND_MISC_NICK_BOOK_RESET = () -> translatable()
			// 想要恢复平常状态,请输入{0}
			.key("floracore.command.misc.nick.book.reset")
			// {0}
			.args(text("/nick reset").decoration(BOLD, true)).append(FULL_STOP).color(BLACK).build();

	Args2<String, NickCommand.SkinType> COMMAND_MISC_NICK_BOOK_NAME_PAGE_RANDOM = (rank, skin) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin.name() + " random");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里以使用随机昵称
				.key("floracore.command.misc.nick.book.name-page.name.random.hover")
				.color(WHITE)
				.build());
		return ARROW.color(BLACK).append(space()).append(translatable()
				// 使用随机昵称
				.key("floracore.command.misc.nick.book.name-page.name.random")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.color(BLACK)
				.build());
	};

	Args2<String, NickCommand.SkinType> COMMAND_MISC_NICK_BOOK_NAME_PAGE_CUSTOM = (rank, skin) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin.name() + " custom");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 使用自定义昵称
				.key("floracore.command.misc.nick.book.name-page.name.custom.hover")
				.color(WHITE)
				.build());
		return ARROW.color(BLACK).append(space()).append(translatable()
				// 点击这里以使用自定义昵称
				.key("floracore.command.misc.nick.book.name-page.name.custom")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.color(BLACK)
				.build());
	};

	Args3<String, NickCommand.SkinType, String> COMMAND_MISC_NICK_BOOK_NAME_PAGE_REUSE = (rank, skin, reuse) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin.name() + " reuse");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 再次使用"{0}"
				.key("floracore.command.misc.nick.book.name-page.name.reuse.hover")
				// {0}
				.args(text(reuse)).color(WHITE).build());
		return ARROW.color(BLACK).append(space()).append(translatable()
				// 点击这里以再次使用"{0}"
				.key("floracore.command.misc.nick.book.name-page.name.reuse")
				// {0}
				.args(text(reuse))
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.color(BLACK)
				.build());
	};

	Args0 COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_1 = () -> translatable()
			// 你已经完成了你的昵称的设置!
			.key("floracore.command.misc.nick.book.finish-page.line.1").color(BLACK).build();

	Args0 COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_1_MESSAGE = () -> translatable()
			// 你已经完成了你的昵称的设置!
			.key("floracore.command.misc.nick.book.finish-page.line.1").color(AQUA).build();

	Args2<String, String> COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_2 = (rank, name) -> {
		Component r = AbstractMessage.formatColoredValue(rank + " " + name);
		return translatable()
				// 当你在游戏时,你的昵称将会变为{0}。你设置的昵称不会在大厅显示。
				.key("floracore.command.misc.nick.book.finish-page.line.2").color(BLACK)
				// {}
				.args(r).build();
	};

	Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_START_TIME_BOOK = (time) -> translatable()
			// 开始时间: {0}
			.key("floracore.command.misc.reports.gui.main.report.chats.chat.start-time")
			.args(text(time, DARK_GREEN))
			.color(BLACK)
			.build();

	Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_END_TIME_BOOK = (time) -> translatable()
			// 结束时间: {0}
			.key("floracore.command.misc.reports.gui.main.report.chats.chat.end-time")
			.args(text(time, DARK_GREEN))
			.color(BLACK)
			.build();

	Args1<Integer> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_1 = (amounts) -> translatable()
			// 共 {0} 条聊天记录
			.key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.1")
			.args(text(amounts).decoration(BOLD, true).decoration(UNDERLINED, true))
			.color(GOLD)
			.build();

	Args0 COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_2 = () -> translatable()
			// 翻页查看
			.key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.2")
			.color(RED)
			.decoration(BOLD, true)
			.decoration(UNDERLINED, true)
			.build();

	Args2<UUID, Boolean> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_3 = (uuid, conclusion) -> {
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里以返回
				.key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.3.hover")
				.color(WHITE)
				.build());
		ClickEvent clickEvent = ClickEvent.runCommand("/rcs " + uuid.toString() + " " + conclusion);
		return translatable()
				// 返回至聊天记录
				.key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.3")
				.clickEvent(clickEvent)
				.hoverEvent(hoverEvent)
				.color(BLACK)
				.decoration(BOLD, true)
				.decoration(UNDERLINED, true)
				.build();
	};

	Args4<String, String, String, Boolean> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_CHAT = (time, player, chat,
                                                                                                   target) -> translatable()
			// {0} {1} : {2}
			.key("floracore.command.misc.reports.gui.main.report.chats.chat.book.chat")
			.args(text(time), text(player).decoration(BOLD, true), text(chat, BLACK).decoration(UNDERLINED, true))
			.color(target ? RED : BLACK)
			.build();

	Args0 COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_1 = () -> translatable()
			// Nick可以让你用不同的用户名来玩,以免被人认出
			.key("floracore.command.misc.nick.book.start-page.line.1").color(BLACK).append(FULL_STOP).build();

	Args0 COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_2 = () -> translatable()
			// 所有规则仍然适用
			.key("floracore.command.misc.nick.book.start-page.line.2").color(BLACK).append(FULL_STOP).build();

	Args0 COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_3 = () -> translatable()
			// 你仍然可以被举报,所有的姓名历史都会被储存
			.key("floracore.command.misc.nick.book.start-page.line.3").color(BLACK).append(FULL_STOP).build();

	Args0 COMMAND_MISC_NICK_BOOK_START_PAGE_ACCEPT_TEXT = () -> {
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里以继续
				.key("floracore.command.misc.nick.book.start-page.accept.hover")
				.color(WHITE)
				.build());
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 1");
		return ARROW.color(BLACK).append(space()).append(translatable()
				// 我明白了,开始设置我的Nick
				.key("floracore.command.misc.nick.book.start-page.accept.text")
				.color(BLACK)
				.decoration(UNDERLINED, true)
				// hover
				.hoverEvent(hoverEvent)
				//click
				.clickEvent(clickEvent)
				.build());
	};

	Args0 COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_1 = () -> translatable()
			// 让我们为你设置您的新昵称吧!
			.key("floracore.command.misc.nick.book.rank-page.line.1").color(BLACK).build();

	Args0 COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_2 = () -> translatable()
			// 首先,你需要选择你希望在Nick后显示为哪一个{0}
			.key("floracore.command.misc.nick.book.rank-page.line.2")
			// {0}
			.args(translatable("floracore.command.misc.nick.book.rank-page.rank").decoration(BOLD, true))
			.append(FULL_STOP)
			.color(BLACK)
			.build();

	Args2<String, String> COMMAND_MISC_NICK_BOOK_RANK_PAGE_RANK = (rankName, rank) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 2 " + rankName);
		Component r = AbstractMessage.formatColoredValue(rank);
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里,显示为 {0} 会员等级
				.key("floracore.command.misc.nick.book.rank-page.rank.hover")
				.args(r.decoration(BOLD, true))
				.color(WHITE)
				.build());
		return ARROW.color(BLACK).append(space()).append(r
				// hover
				.hoverEvent(hoverEvent)
				// click
				.clickEvent(clickEvent));
	};

	Args1<String> COMMAND_NICK_SETUP_RANK = (rank) -> {
		Component r = AbstractMessage.formatColoredValue(rank);
		return AbstractMessage.prefixed(translatable()
				// 你的昵称会员等级已设置为 {0} !
				.key("floracore.command.nick.setup.rank")
				// {0}
				.args(r).color(AQUA));
	};

	Args0 COMMAND_MISC_NICK_BOOK_SKIN_PAGE_LINE_1 = () -> translatable()
			// 芜湖!现在,你希望在Nick后使用哪种皮肤?
			.key("floracore.command.misc.nick.book.skin-page.line.1")
			// {0}
			.args(translatable("floracore.command.misc.nick.book.skin-page.skin").decoration(BOLD, true))
			.color(BLACK)
			.build();

	Args1<String> COMMAND_MISC_NICK_BOOK_SKIN_PAGE_NORMAL = (rank) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 3 " + rank + " normal");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里以使用你自己的皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.normal.hover")
				.color(WHITE)
				.build());
		return ARROW.color(BLACK).append(space()).append(translatable()
				// 我自己的皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.normal")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.color(BLACK)
				.build());
	};

	Args1<String> COMMAND_MISC_NICK_BOOK_SKIN_PAGE_STEVE_ALEX = (rank) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 3 " + rank + " steve_alex");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里以使用Steve/Alex的皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.steve-alex.hover")
				.color(WHITE)
				.build());
		return ARROW.color(BLACK).append(space()).append(translatable()
				// Steve/Alex的皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.steve-alex")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.color(BLACK)
				.build());
	};

	Args0 COMMAND_MISC_NICK_SKIN_STEVE_ALEX = () -> translatable()
			.key("floracore.command.misc.nick.book.skin-page.skin.steve-alex").build();
	Args0 COMMAND_MISC_NICK_SKIN_RANDOM = () -> translatable()
			.key("floracore.command.misc.nick.book.skin-page.skin.random").build();
	Args0 COMMAND_MISC_NICK_SKIN_NORMAL = () -> translatable()
			.key("floracore.command.misc.nick.book.skin-page.skin.normal.pure").build();
	Args1<String> COMMAND_MISC_NICK_SKIN_REUSE = (skin) -> translatable()
			.key("floracore.command.misc.nick.book.skin-page.skin.reuse.pure").args(text(skin)).build();

	Args1<String> COMMAND_MISC_NICK_BOOK_SKIN_PAGE_RANDOM = (rank) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 3 " + rank + " random");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 点击这里以使用随机皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.random.hover")
				.color(WHITE)
				.build());
		return ARROW.color(BLACK).append(space()).append(translatable()
				// 随机皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.random")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				.color(BLACK)
				.build());
	};

	Args2<String, String> COMMAND_MISC_NICK_BOOK_SKIN_PAGE_REUSE = (rank, reuse) -> {
		ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 3 " + rank + " reuse");
		HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
				// 重新使用 {0} 的皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.reuse.hover")
				// {0}
				.args(text(reuse)).color(WHITE).build());
		return ARROW.color(BLACK).append(space()).append(translatable()
				// 点击这里以重新使用 {0} 的皮肤
				.key("floracore.command.misc.nick.book.skin-page.skin.reuse")
				.hoverEvent(hoverEvent)
				.clickEvent(clickEvent)
				// {0}
				.args(text(reuse))
				.color(BLACK)
				.build());
	};
}
