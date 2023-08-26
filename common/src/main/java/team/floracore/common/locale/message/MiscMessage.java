package team.floracore.common.locale.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import team.floracore.common.plugin.bootstrap.FloraCoreBootstrap;
import team.floracore.common.util.DescParseTickFormat;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public interface MiscMessage extends AbstractMessage {
	Component PREFIX_BROADCAST = text()
			// [公告]
			.color(GRAY)
			.append(text('['))
			.append(text().decoration(BOLD, true)
					.append(translatable("floracore.command.misc.server.broadcast.prefix").color(AQUA)))
			.append(text(']'))
			.build();

	Component PREFIX_PARTY = text()
			// 组队 >
			.append(translatable("floracore.command.misc.party.prefix", BLUE))
			.append(space())
			.append(AbstractMessage.ARROW_LIGHT.color(DARK_GRAY))
			.build();

	Component PREFIX_PARTY_LIGHT = text()
			// 组队
			.append(translatable("floracore.command.misc.party.prefix", BLUE)).build();

	Component PREFIX_STAFF = text()
			// 员工 >
			.append(translatable("floracore.command.misc.staff.prefix", AQUA))
			.append(space())
			.append(AbstractMessage.ARROW_LIGHT.color(DARK_GRAY))
			.build();

	Component PREFIX_STAFF_LIGHT = text()
			// 员工
			.append(translatable("floracore.command.misc.staff.prefix", AQUA)).build();

	Component PREFIX_BUILDER = text()
			// 建筑组 >
			.append(translatable("floracore.command.misc.builder.prefix", DARK_AQUA))
			.append(space())
			.append(AbstractMessage.ARROW_LIGHT.color(DARK_GRAY))
			.build();

	Component PREFIX_BUILDER_LIGHT = text()
			// 建筑组
			.append(translatable("floracore.command.misc.builder.prefix", DARK_AQUA)).build();

	Component PREFIX_ADMIN = text()
			// 管理 >
			.append(translatable("floracore.command.misc.admin.prefix", RED))
			.append(space())
			.append(AbstractMessage.ARROW_LIGHT.color(DARK_GRAY))
			.build();

	Args1<Component> PREFIX_CUSTOM = (prefix) -> text()
			// CUSTOM >
			.append(prefix)
			.append(space())
			.append(AbstractMessage.ARROW_LIGHT.color(DARK_GRAY))
			.build();

	Component PREFIX_ADMIN_LIGHT = text()
			// 管理
			.append(translatable("floracore.command.misc.admin.prefix", RED)).build();

	Component PREFIX_ALL_LIGHT = text()
			// 管理
			.append(translatable("floracore.command.misc.all.prefix", WHITE)).build();

	Component CLICK_TP = text().append(translatable("floracore.command.misc.click-tp", YELLOW)).build();
	Component CLICK_JOIN = text().append(translatable("floracore.command.misc.click-join", YELLOW)).build();

	Args0 PARTY_HORIZONTAL_LINE = () -> HORIZONTAL_LINES.decoration(STRIKETHROUGH, true).color(BLUE);
	Args0 ADMIN_HORIZONTAL_LINE = () -> HORIZONTAL_LINES.decoration(STRIKETHROUGH, true).color(RED);
	Args0 BUILDER_HORIZONTAL_LINE = () -> HORIZONTAL_LINES.decoration(STRIKETHROUGH, true).color(DARK_AQUA);
	Args0 STAFF_HORIZONTAL_LINE = () -> HORIZONTAL_LINES.decoration(STRIKETHROUGH, true).color(AQUA);

	Args1<FloraCoreBootstrap> STARTUP_BANNER = bootstrap -> {
		// FloraCore v{} is Running.
		Component infoLine1 = text()
				// FloraCore
				.append(text("Fl", AQUA)).append(text("ora", DARK_GREEN))
				.append(text("Core", YELLOW)).append(space())
				// v{}
				.append(text("v" + bootstrap.getVersion(), AQUA)).append(space())
				// Running
				.append(text("Running on", DARK_GRAY)).append(space())
				.append(text(bootstrap.getType().getFriendlyName(), YELLOW)).build();

		JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();

		return join(joinConfig, text().append(infoLine1).build());
	};

	Args1<String> TRANSLATIONS_INSTALLING_SPECIFIC = name -> AbstractMessage.prefixed(translatable()
			// 正在安装语言 {0}...
			.key("floracore.command.translations.installing-specific")
			.color(GREEN)
			.args(text(name)));

	Args1<String> TRANSLATIONS_DOWNLOAD_ERROR = name -> AbstractMessage.prefixed(text()
			// 无法下载 {0} 的翻译
			.color(RED)
			.append(translatable("floracore.command.translations.download-error",
					text(name, DARK_RED)))
			.append(FULL_STOP)
			.append(space())
			// 检查控制台是否有错误
			.append(translatable("floracore.command.misc.check-console-for-errors"))
			.append(FULL_STOP));

	Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE = bootstrap -> AbstractMessage.prefixed(
			text("Checking FloraCore version information...").color(AQUA));
	Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE_NEWEST = bootstrap -> AbstractMessage.prefixed(
			text("The current version of FloraCore is the latest version!").color(GREEN));
	Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE_FAILED = bootstrap -> AbstractMessage.prefixed(
			text("FloraCore version check failed!").color(RED));
	Args2<FloraCoreBootstrap, String> STARTUP_CHECKING_UPDATE_OUTDATED =
			(bootstrap, version) -> AbstractMessage.prefixed(
					text("The current FloraCore version is outdated! Latest version: ").color(RED)
							.append(text("v" + version).color(DARK_AQUA)));

	Args0 NO_PERMISSION_FOR_SUBCOMMANDS = () -> AbstractMessage.prefixed(translatable()
			// 你没有权限使用任何子命令
			.key("floracore.commandsystem.no-permission-subcommands")
			.color(RED));

	Args0 COMMAND_CURRENT_SERVER_FORBIDDEN = () -> AbstractMessage.prefixed(translatable()
			// 你不能在当前服务器使用此命令!
			.key("floracore.commandsystem.current-server-forbidden")
			.color(RED));

	Args0 COMMAND_NO_PERMISSION = () -> AbstractMessage.prefixed(translatable()
			// 你没有使用此命令的权限！
			.key("floracore.commandsystem.no-permission")
			.color(RED));

	Args1<String> COMMAND_INVALID_COMMAND_SYNTAX = correctSyntax -> AbstractMessage.prefixed(text()
			// 命令语法错误,你可能想要输入：{0}
			.color(RED)
			.append(translatable("floracore.commandsystem.invalid-command-syntax",
					text(correctSyntax, DARK_AQUA)))
			.append(FULL_STOP));

	Args2<String, String> COMMAND_INVALID_COMMAND_SENDER = (commandSender, requiredSender) -> AbstractMessage.prefixed(
			text()
					// {0} 不允许执行该命令,必须由 {1} 执行
					.color(RED)
					.append(translatable("floracore.commandsystem.invalid-command-sender",
							text(commandSender),
							text(requiredSender)))
					.append(FULL_STOP));

	Args0 COMMAND_SERVER_DATA_TYPE = () -> translatable("floracore.command.server.data.type");
	Args0 COMMAND_SERVER_DATA_AUTO_SYNC_1 = () -> translatable("floracore.command.server.data.autosync.1");
	Args0 COMMAND_SERVER_DATA_AUTO_SYNC_2 = () -> translatable("floracore.command.server.data.autosync.2");
	Args0 COMMAND_SERVER_DATA_ACTIVE_TIME = () -> translatable("floracore.command.server.data.active-time");

	Args0 COMMAND_MISC_GAMEMODE_SURVIVAL = () -> translatable("floracore.command.misc.gamemode.survival");
	Args0 COMMAND_MISC_GAMEMODE_CREATIVE = () -> translatable("floracore.command.misc.gamemode.creative");
	Args0 COMMAND_MISC_GAMEMODE_ADVENTURE = () -> translatable("floracore.command.misc.gamemode.adventure");
	Args0 COMMAND_MISC_GAMEMODE_SPECTATOR = () -> translatable("floracore.command.misc.gamemode.spectator");

	Args0 COMMAND_MISC_WEATHER = () -> translatable("floracore.command.misc.weather");
	Args0 COMMAND_MISC_WEATHER_SUM = () -> translatable("floracore.command.misc.weather.sun");
	Args0 COMMAND_MISC_WEATHER_STORM = () -> translatable("floracore.command.misc.weather.storm");

	Args0 COMMAND_MISC_WORLD_INVALID = () -> AbstractMessage.prefixed(translatable()
			// 无效的世界
			.key("floracore.command.misc.world.invalid")
			.color(RED));

	Args1<String> ILLEGAL_DATE_ERROR = invalid -> AbstractMessage.prefixed(translatable()
			// 无法解析日期 {0}
			.key("floracore.command.misc.date-parse-error")
			.color(RED)
			.args(text(invalid, DARK_RED))
			.append(FULL_STOP));

	Args0 PAST_DATE_ERROR = () -> AbstractMessage.prefixed(translatable()
			// 不能设置已经过去的日期\!
			.key("floracore.command.misc.date-in-past-error")
			.color(RED));

	Args0 COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION = () -> AbstractMessage.prefixed(translatable()
			// 执行命令异常
			.key("floracore.command.misc.execute-command-exception")
			.color(RED));

	Args1<String> COMMAND_MISC_INVALID_NUMBER = (number) -> AbstractMessage.prefixed(translatable()
			// {0} 不是有效的数字
			.key("floracore.command.misc.invalid-number")
			// {0}
			.args(text(number, DARK_RED))
			.color(RED));

	Args1<String> COMMAND_MISC_INVALID_FORMAT = (format) -> AbstractMessage.prefixed(translatable()
			// {0} 不是有效的格式
			.key("floracore.command.misc.invalid-format")
			// {0}
			.args(text(format, DARK_RED))
			.color(RED));

	Args0 COMMAND_MISC_GUI_PREVIOUS_PAGE = () -> translatable()
			// 上一页
			.key("floracore.command.misc.gui.previous-page").color(GREEN).build();

	Args0 COMMAND_MISC_GUI_NEXT_PAGE = () -> translatable()
			// 下一页
			.key("floracore.command.misc.gui.next-page").color(GREEN).build();

	Args0 COMMAND_MISC_GUI_BACK = () -> translatable()
			// 返回
			.key("floracore.command.misc.gui.back").color(YELLOW).build();

	Args1<Integer> COMMAND_MISC_GUI_TURN_TO_PAGE = (page) -> translatable()
			// 转到第 {0} 页
			.key("floracore.command.misc.gui.turn-to-page").args(text(page)).color(GRAY).build();

	Args1<String> PLAYER_NOT_FOUND = id -> AbstractMessage.prefixed(translatable()
			// 无法找到 {0} 这名玩家
			.key("floracore.command.misc.loading.error.player-not-found")
			.color(RED)
			.args(text(id, DARK_RED))
			.append(FULL_STOP));

	Args1<String> SERVER_NOT_FOUND = id -> AbstractMessage.prefixed(translatable()
			// 无法找到 {0} 这个服务器
			.key("floracore.command.misc.loading.error.server-not-found")
			.color(RED)
			.args(text(id, DARK_RED))
			.append(FULL_STOP));

	Args1<Long> DURATION_FORMAT = (ticks) -> translatable()
			// {0} 或 {1}（或 {2} ）
			.key("floracore.duration.format")
			.color(RED)
			.args(text(DescParseTickFormat.format24(ticks)).color(GREEN),
					text(DescParseTickFormat.format12(ticks)).color(GREEN),
					text(DescParseTickFormat.formatTicks(ticks)).color(GREEN))
			.build();

	Args2<Integer, String> GENERIC_HTTP_REQUEST_FAILURE = (code, message) -> AbstractMessage.prefixed(text()
			// "&cUnable to communicate with the web app. (response code &4{}&c, message='{}')"
			.color(RED)
			.append(translatable("floracore.command.misc.webapp-unable-to-communicate"))
			.append(FULL_STOP)
			.append(space())
			.append(text()
					.append(OPEN_BRACKET)
					.append(translatable("floracore.command.misc.response-code-key"))
					.append(space())
					.append(text(code))
					.append(text(", "))
					.append(translatable("floracore.command.misc.error-message-key"))
					.append(text("='"))
					.append(text(message))
					.append(text("'"))
					.append(CLOSE_BRACKET)
			)
	);

	Args0 GENERIC_HTTP_UNKNOWN_FAILURE = () -> AbstractMessage.prefixed(text()
			// "&cUnable to communicate with the web app. Check the console for errors."
			.color(RED)
			.append(translatable("floracore.command.misc.webapp-unable-to-communicate"))
			.append(FULL_STOP)
			.append(space())
			.append(translatable("floracore.command.misc.check-console-for-errors"))
			.append(FULL_STOP)
	);

	Args0 MISC_GETTING = () -> AbstractMessage.prefixed(text()
			// 获取中...
			.color(AQUA)
			.append(translatable("floracore.command.misc.getting"))
	);

	Args0 CHECK_CONSOLE_FOR_ERRORS = () -> AbstractMessage.prefixed(translatable()
			.key("floracore.command.misc.check-console-for-errors")
			.color(RED));


	Args1<String> CHAT_RESULTS_URL = url -> {
		JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
		return join(joinConfig,
				// "&a聊天记录链接"
				// <link>
				AbstractMessage.prefixed(translatable()
						.key("floracore.command.chat.url")
						.color(AQUA)
						.append(text(':'))),
				text()
						.content(url)
						.color(GREEN)
						.clickEvent(ClickEvent.openUrl(url))
		);
	};
}
