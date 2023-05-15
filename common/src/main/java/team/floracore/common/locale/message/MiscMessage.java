package team.floracore.common.locale.message;

import net.kyori.adventure.text.*;
import team.floracore.common.plugin.bootstrap.*;

import java.util.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;
import static team.floracore.common.util.ReflectionWrapper.*;

public interface MiscMessage extends AbstractMessage {
    Component PREFIX_BROADCAST = text()
            // [公告]
            .color(GRAY).append(text('[')).append(text().decoration(BOLD, true).append(translatable("floracore.command.misc.server.broadcast.prefix").color(AQUA))).append(text(']')).build();

    Args1<FloraCoreBootstrap> STARTUP_BANNER = bootstrap -> {
        // FloraCore v{} is Running.
        Component infoLine1 = text()
                // FloraCore
                .append(text("Fl", AQUA)).append(text("ora", DARK_GREEN)).append(text("Core", YELLOW)).append(space())
                // v{}
                .append(text("v" + bootstrap.getVersion(), AQUA)).append(space())
                // Running
                .append(text("is Running on", DARK_GRAY)).append(space())
                // server version
                .append(text(getVersion(), DARK_GRAY)).build();

        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();

        return join(joinConfig, text().append(infoLine1).build());
    };

    Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE = bootstrap -> AbstractMessage.prefixed(text("Checking FloraCore version information...").color(AQUA));
    Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE_NEWEST = bootstrap -> AbstractMessage.prefixed(text("The current version of FloraCore is the latest version!").color(GREEN));
    Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE_FAILED = bootstrap -> AbstractMessage.prefixed(text("FloraCore version check failed!").color(RED));
    Args2<FloraCoreBootstrap, String> STARTUP_CHECKING_UPDATE_OUTDATED = (bootstrap, version) -> AbstractMessage.prefixed(text("The current FloraCore version is outdated! Latest version: ").color(RED).append(text("v" + version).color(DARK_AQUA)));

    Args0 TRANSLATIONS_SEARCHING = () -> AbstractMessage.prefixed(translatable()
            // 正在搜索可用的翻译, 请稍候...
            .key("floracore.command.translations.searching").color(GRAY));

    Args0 TRANSLATIONS_SEARCHING_ERROR = () -> AbstractMessage.prefixed(text()
            // 无法获得可用翻译的列表
            .color(RED).append(translatable("floracore.command.translations.searching-error")).append(FULL_STOP)
            // 检查控制台是否有错误
            .append(space()).append(translatable("floracore.command.misc.check-console-for-errors")).append(FULL_STOP));


    Args1<Collection<String>> INSTALLED_TRANSLATIONS = locales -> AbstractMessage.prefixed(translatable()
            // 已安装的翻译
            .key("floracore.command.translations.installed-translations").color(GREEN)
            // info
            .append(text(':')).append(space())
            // list
            .append(AbstractMessage.formatStringList(locales)));


    Args4<String, String, Integer, List<String>> AVAILABLE_TRANSLATIONS_ENTRY = (tag, name, percentComplete, contributors) -> AbstractMessage.prefixed(text()
            // - {} ({}) - 已翻译{}% - 由 {}
            .color(GRAY).append(text('-')).append(space()).append(text(tag, AQUA)).append(space()).append(OPEN_BRACKET)
            // 语种
            .append(text(name, WHITE)).append(CLOSE_BRACKET).append(text(" - "))
            // 翻译进度
            .append(translatable("floracore.command.translations.percent-translated", text(percentComplete, GREEN))).apply(builder -> {
                if (!contributors.isEmpty()) {
                    builder.append(text(" - "));
                    builder.append(translatable("floracore.command.translations.translations-by"));
                    builder.append(space());
                    builder.append(AbstractMessage.formatStringList(contributors));
                }
            }));

    Args0 TRANSLATIONS_DOWNLOAD_PROMPT = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, AbstractMessage.prefixed(translatable()
                // 使用 {0} 下载并安装由社区提供的翻译的最新版本
                .key("floracore.command.translations.download-prompt").color(AQUA).args(text("/fc translations install", GREEN)).append(FULL_STOP)), AbstractMessage.prefixed(translatable()
                // 请注意, 此操作将会覆盖您对这些语言做出的任何更改
                .key("floracore.command.translations.download-override-warning").color(GRAY).append(FULL_STOP)));
    };

    Args0 AVAILABLE_TRANSLATIONS_HEADER = () -> AbstractMessage.prefixed(translatable()
            // 可用的翻译
            .key("floracore.command.translations.available-translations").color(GREEN).append(text(':')));

    Args1<String> TRANSLATIONS_INSTALLING_SPECIFIC = name -> AbstractMessage.prefixed(translatable()
            // 正在安装语言 {0}...
            .key("floracore.command.translations.installing-specific").color(GREEN).args(text(name)));

    Args1<String> TRANSLATIONS_DOWNLOAD_ERROR = name -> AbstractMessage.prefixed(text()
            // 无法下载 {0} 的翻译
            .color(RED).append(translatable("floracore.command.translations.download-error", text(name, DARK_RED))).append(FULL_STOP).append(space())
            // 检查控制台是否有错误
            .append(translatable("floracore.command.misc.check-console-for-errors")).append(FULL_STOP));

    Args0 TRANSLATIONS_INSTALLING = () -> AbstractMessage.prefixed(translatable()
            // 正在安装翻译, 请稍候...
            .key("floracore.command.translations.installing").color(AQUA));

    Args0 TRANSLATIONS_INSTALL_COMPLETE = () -> AbstractMessage.prefixed(translatable()
            // 安装已完成
            .key("floracore.command.translations.install-complete").color(AQUA).append(FULL_STOP));

    Args0 RELOAD_CONFIG_SUCCESS = () -> AbstractMessage.prefixed(translatable()
            // 已重新加载配置文件
            .key("floracore.command.reload-config.success").color(GREEN).append(FULL_STOP).append(space())
            // 某些选项仅在服务器重新启动后才应用
            .append(text().color(GRAY).append(OPEN_BRACKET).append(translatable("floracore.command.reload-config.restart-note")).append(CLOSE_BRACKET)));

    Args0 NO_PERMISSION_FOR_SUBCOMMANDS = () -> AbstractMessage.prefixed(translatable()
            // 你没有权限使用任何子命令
            .key("floracore.commandsystem.no-permission-subcommands").color(RED).append(FULL_STOP));

    Args0 COMMAND_CURRENT_SERVER_FORBIDDEN = () -> AbstractMessage.prefixed(translatable()
            // 你不能在当前服务器使用此命令!
            .key("floracore.commandsystem.current-server-forbidden").color(RED));

    Args0 COMMAND_NO_PERMISSION = () -> AbstractMessage.prefixed(translatable()
            // 您没有使用此命令的权限！
            .key("floracore.commandsystem.no-permission").color(RED));

    Args1<String> COMMAND_INVALID_COMMAND_SYNTAX = correctSyntax -> AbstractMessage.prefixed(text()
            // 命令语法错误，您可能想要输入：{0}
            .color(RED).append(translatable("floracore.commandsystem.invalid-command-syntax", text(correctSyntax, DARK_AQUA))).append(FULL_STOP));

    Args2<String, String> COMMAND_INVALID_COMMAND_SENDER = (commandSender, requiredSender) -> AbstractMessage.prefixed(text()
            // {0} 不允许执行该命令，必须由 {1} 执行
            .color(RED).append(translatable("floracore.commandsystem.invalid-command-sender", text(commandSender), text(requiredSender))).append(FULL_STOP));

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
            .key("floracore.command.misc.world.invalid").color(RED));

    Args1<String> ILLEGAL_DATE_ERROR = invalid -> AbstractMessage.prefixed(translatable()
            // 无法解析日期 {0}
            .key("floracore.command.misc.date-parse-error").color(RED).args(text(invalid, DARK_RED)).append(FULL_STOP));

    Args0 PAST_DATE_ERROR = () -> AbstractMessage.prefixed(translatable()
            // 不能设置已经过去的日期\!
            .key("floracore.command.misc.date-in-past-error").color(RED));

    Args0 COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION = () -> AbstractMessage.prefixed(translatable()
            // 执行命令异常
            .key("floracore.command.misc.execute-command-exception").color(RED));

    Args1<String> COMMAND_MISC_INVALID_NUMBER = (number) -> AbstractMessage.prefixed(translatable()
            // {0} 不是有效的数字
            .key("floracore.command.misc.invalid-number")
            // {0}
            .args(text(number)).color(RED));

    Args0 CHECK_TP = () -> translatable("floracore.command.misc.check-tp", YELLOW);

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

}
