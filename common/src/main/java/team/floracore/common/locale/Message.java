package team.floracore.common.locale;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.*;
import net.kyori.adventure.text.serializer.legacy.*;
import org.bukkit.*;
import org.floracore.api.data.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

import java.time.*;
import java.util.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;
import static team.floracore.common.util.ReflectionWrapper.*;

public interface Message {
    TextComponent OPEN_BRACKET = Component.text('(');
    TextComponent CLOSE_BRACKET = Component.text(')');
    TextComponent FULL_STOP = Component.text('.');
    TextComponent ARROW = Component.text('➤');

    Component PREFIX_COMPONENT = text()
            // [FC]
            .color(GRAY).append(text('[')).append(text().decoration(BOLD, true).append(text('F', AQUA)).append(text('C', YELLOW))).append(text(']')).build();

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

    Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE = bootstrap -> prefixed(text("Checking FloraCore version information...").color(AQUA));
    Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE_NEWEST = bootstrap -> prefixed(text("The current version of FloraCore is the latest version!").color(GREEN));
    Args1<FloraCoreBootstrap> STARTUP_CHECKING_UPDATE_FAILED = bootstrap -> prefixed(text("FloraCore version check failed!").color(RED));
    Args2<FloraCoreBootstrap, String> STARTUP_CHECKING_UPDATE_OUTDATED = (bootstrap, version) -> prefixed(text("The current FloraCore version is outdated! Latest version: ").color(RED).append(text("v" + version).color(DARK_AQUA)));

    Args0 TRANSLATIONS_SEARCHING = () -> prefixed(translatable()
            // 正在搜索可用的翻译, 请稍候...
            .key("floracore.command.translations.searching").color(GRAY));

    Args0 TRANSLATIONS_SEARCHING_ERROR = () -> prefixed(text()
            // 无法获得可用翻译的列表
            .color(RED).append(translatable("floracore.command.translations.searching-error")).append(FULL_STOP)
            // 检查控制台是否有错误
            .append(space()).append(translatable("floracore.command.misc.check-console-for-errors")).append(FULL_STOP));


    Args1<Collection<String>> INSTALLED_TRANSLATIONS = locales -> prefixed(translatable()
            // 已安装的翻译
            .key("floracore.command.translations.installed-translations").color(GREEN)
            // info
            .append(text(':')).append(space())
            // list
            .append(formatStringList(locales)));


    Args4<String, String, Integer, List<String>> AVAILABLE_TRANSLATIONS_ENTRY = (tag, name, percentComplete, contributors) -> prefixed(text()
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
                    builder.append(formatStringList(contributors));
                }
            }));

    Args0 TRANSLATIONS_DOWNLOAD_PROMPT = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, prefixed(translatable()
                // 使用 {0} 下载并安装由社区提供的翻译的最新版本
                .key("floracore.command.translations.download-prompt").color(AQUA).args(text("/fc translations install", GREEN)).append(FULL_STOP)), prefixed(translatable()
                // 请注意, 此操作将会覆盖您对这些语言做出的任何更改
                .key("floracore.command.translations.download-override-warning").color(GRAY).append(FULL_STOP)));
    };

    Args0 AVAILABLE_TRANSLATIONS_HEADER = () -> prefixed(translatable()
            // 可用的翻译
            .key("floracore.command.translations.available-translations").color(GREEN).append(text(':')));

    Args1<String> TRANSLATIONS_INSTALLING_SPECIFIC = name -> prefixed(translatable()
            // 正在安装语言 {0}...
            .key("floracore.command.translations.installing-specific").color(GREEN).args(text(name)));

    Args1<String> TRANSLATIONS_DOWNLOAD_ERROR = name -> prefixed(text()
            // 无法下载 {0} 的翻译
            .color(RED).append(translatable("floracore.command.translations.download-error", text(name, DARK_RED))).append(FULL_STOP).append(space())
            // 检查控制台是否有错误
            .append(translatable("floracore.command.misc.check-console-for-errors")).append(FULL_STOP));

    Args0 TRANSLATIONS_INSTALLING = () -> prefixed(translatable()
            // 正在安装翻译, 请稍候...
            .key("floracore.command.translations.installing").color(AQUA));

    Args0 TRANSLATIONS_INSTALL_COMPLETE = () -> prefixed(translatable()
            // 安装已完成
            .key("floracore.command.translations.install-complete").color(AQUA).append(FULL_STOP));

    Args0 RELOAD_CONFIG_SUCCESS = () -> prefixed(translatable()
            // 已重新加载配置文件
            .key("floracore.command.reload-config.success").color(GREEN).append(FULL_STOP).append(space())
            // 某些选项仅在服务器重新启动后才应用
            .append(text().color(GRAY).append(OPEN_BRACKET).append(translatable("floracore.command.reload-config.restart-note")).append(CLOSE_BRACKET)));

    Args0 NO_PERMISSION_FOR_SUBCOMMANDS = () -> prefixed(translatable()
            // 你没有权限使用任何子命令
            .key("floracore.commandsystem.no-permission-subcommands").color(RED).append(FULL_STOP));

    Args0 COMMAND_CURRENT_SERVER_FORBIDDEN = () -> prefixed(translatable()
            // 你不能在当前服务器使用此命令!
            .key("floracore.commandsystem.current-server-forbidden").color(RED));

    Args0 COMMAND_NO_PERMISSION = () -> prefixed(translatable()
            // 您没有使用此命令的权限！
            .key("floracore.commandsystem.no-permission").color(RED));

    Args1<String> COMMAND_INVALID_COMMAND_SYNTAX = correctSyntax -> prefixed(text()
            // 命令语法错误，您可能想要输入：{0}
            .color(RED).append(translatable("floracore.commandsystem.invalid-command-syntax", text(correctSyntax, DARK_AQUA))).append(FULL_STOP));

    Args2<String, String> COMMAND_INVALID_COMMAND_SENDER = (commandSender, requiredSender) -> prefixed(text()
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

    Args2<Boolean, String> COMMAND_FLY = (status, target) -> prefixed(translatable()
            // {1} 的飞行模式被设置为 {0}
            .key("floracore.command.fly").color(AQUA)
            // 开 / 关
            .args(translatable(status ? "floracore.command.misc.on" : "floracore.command.misc.off").color(GREEN), text(target).color(RED)).append(FULL_STOP));

    Args2<Boolean, String> COMMAND_FLY_FROM = (status, from) -> prefixed(translatable()
            // {1} 将您的游戏模式设置为 {0}
            .key("floracore.command.fly.from").color(AQUA)
            // 开 / 关
            .args(translatable(status ? "floracore.command.misc.on" : "floracore.command.misc.off").color(GREEN), text(from).color(GREEN)).append(FULL_STOP));
    Args2<Component, String> COMMAND_GAMEMODE = (mode, target) -> prefixed(translatable()
            // 将{1}的游戏模式设置为{0}
            .key("floracore.command.gamemode").color(AQUA).args(mode.color(GREEN), text(target).color(GREEN)).append(FULL_STOP));

    Args2<Component, String> COMMAND_GAMEMODE_FROM = (mode, from) -> prefixed(translatable()
            // {1} 将您的游戏模式设置为 {0}
            .key("floracore.command.gamemode.from").color(AQUA).args(mode.color(GREEN), text(from).color(GREEN)).append(FULL_STOP));

    Args1<String> COMMAND_GAMEMODE_NOSUCH = (mode) -> prefixed(translatable()
            // {0} 不是合法的游戏模式
            .key("floracore.command.gamemode.nosuch").color(RED).args(text(mode).color(GREEN)).append(FULL_STOP));

    Args0 COMMAND_MISC_WEATHER = () -> translatable("floracore.command.misc.weather");
    Args0 COMMAND_MISC_WEATHER_SUM = () -> translatable("floracore.command.misc.weather.sun");
    Args0 COMMAND_MISC_WEATHER_STORM = () -> translatable("floracore.command.misc.weather.storm");

    Args1<String> COMMAND_WEATHER_NOSUCH = (weather) -> prefixed(translatable()
            // {0} 不是合法的天气类型
            .key("floracore.command.weather.nosuch").color(RED).args(text(weather).color(GREEN)).append(FULL_STOP));

    Args2<World, Component> COMMAND_WEATHER_NORMAL = (world, weather) -> prefixed(translatable()
            // 你将 {0} 的天气设为 {1}
            .key("floracore.command.weather.normal").color(AQUA).args(text(world.getName()).color(GREEN), weather.color(GREEN)).append(FULL_STOP));

    Args3<World, Component, String> COMMAND_WEATHER_TIME = (world, weather, time) -> prefixed(translatable()
            // 你将 {0} 的天气设为 {1}，持续 {2} 秒
            .key("floracore.command.weather.time").color(AQUA).args(text(world.getName()).color(GREEN), weather.color(GREEN), text(time).color(GREEN)).append(FULL_STOP));

    Args1<Long> DURATION_FORMAT = (ticks) -> translatable()
            // {0} 或 {1}（或 {2} ）
            .key("floracore.duration.format").color(RED).args(text(DescParseTickFormat.format24(ticks)).color(GREEN), text(DescParseTickFormat.format12(ticks)).color(GREEN), text(DescParseTickFormat.formatTicks(ticks)).color(GREEN)).build();

    Args0 COMMAND_MISC_WORLD_INVALID = () -> prefixed(translatable()
            // 无效的世界
            .key("floracore.command.misc.world.invalid").color(RED));

    Args2<World, Component> COMMAND_TIME_WORLD_CURRENT = (world, time) -> prefixed(translatable()
            // 当前 {0} 的时间是 {1}
            .key("floracore.command.time.world.current").color(AQUA).args(text(world.getName()).color(GREEN), time));

    Args2<String, Component> COMMAND_TIME_SET = (world, time) -> prefixed(translatable()
            // {0} 的时间被设置为 {1}
            .key("floracore.command.time.set").color(AQUA).args(text(world).color(GREEN), time));

    Args2<String, Component> COMMAND_TIME_ADD = (world, time) -> prefixed(translatable()
            // {0} 的时间已被 {1} 快进
            .key("floracore.command.time.add").color(AQUA).args(text(world).color(GREEN), time));

    Args0 COMMAND_HAT_ARMOR = () -> prefixed(translatable()
            // 你无法将这个物品当做帽子戴上!
            .key("floracore.command.hat.armor").color(RED));

    Args0 COMMAND_HAT_CURSE = () -> prefixed(translatable()
            // 你不能移除带有绑定诅咒的帽子!
            .key("floracore.command.hat.curse").color(RED));

    Args0 COMMAND_HAT_EMPTY = () -> prefixed(translatable()
            // 你现在没有戴帽子!
            .key("floracore.command.hat.empty").color(RED));

    Args0 COMMAND_HAT_FAIL = () -> prefixed(translatable()
            // 你必须把想要戴的帽子拿在手中!
            .key("floracore.command.hat.fail").color(RED));

    Args0 COMMAND_HAT_REMOVED = () -> prefixed(translatable()
            // 你的帽子已被移除
            .key("floracore.command.hat.removed").color(AQUA).append(FULL_STOP));

    Args0 COMMAND_HAT_PLACED = () -> prefixed(translatable()
            // 你戴上了新帽子
            .key("floracore.command.hat.placed").color(AQUA).append(FULL_STOP));

    Args0 COMMAND_INVSEE_SELF = () -> prefixed(translatable()
            // 你只能查看其他玩家的物品栏!
            .key("floracore.command.invsee.self").color(RED));

    Args1<String> COMMAND_INVSEE = (target) -> prefixed(translatable()
            // 你打开了 {0} 的物品栏
            .key("floracore.command.invsee").color(AQUA).args(text(target).color(GREEN)).append(FULL_STOP));

    Args1<String> DATA_NONE = target -> prefixed(translatable()
            // {0} 无记录的数据
            .key("floracore.command.generic.data.none").color(AQUA).args(text(target)).append(FULL_STOP));

    Args1<String> DATA_HEADER = target -> prefixed(translatable()
            // {0} 的数据信息:
            .key("floracore.command.generic.data.info.title").color(AQUA).args(text(target)));

    Args1<String> PLAYER_NOT_FOUND = id -> prefixed(translatable()
            // 无法找到 {0} 这名玩家
            .key("floracore.command.misc.loading.error.player-not-found").color(RED).args(text(id, DARK_RED)).append(FULL_STOP));

    Args4<String, String, String, Long> DATA_ENTRY = (type, key, value, expiry) -> {
        Instant instant = Instant.ofEpochMilli(expiry);
        Instant now = Instant.now();
        Duration timeElapsed = Duration.between(now, instant);
        return prefixed(text().append(text(type, GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(text(key, AQUA)).append(text(" - ", WHITE)).append(text().color(WHITE).append(text('\'')).append(text(value)).append(text('\''))).apply(builder -> {
            if (expiry > 0) {
                builder.append(space());
                builder.append(text().color(DARK_GRAY).append(OPEN_BRACKET).append(translatable()
                        // 过期时间
                        .key("floracore.command.generic.info.expires-in").color(GRAY).append(space()).append(text().color(AQUA).append(DurationFormatter.CONCISE.format(timeElapsed)))).append(CLOSE_BRACKET));
            }
        }));
    };

    Args2<Component, String> SERVER_DATA_ENTRY = (key, value) -> prefixed(text().append(key.color(GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(text(value, WHITE)).apply(builder -> {
    }));
    Args2<Component, Component> SERVER_DATA_ENTRY_1 = (key, value) -> prefixed(text().append(key.color(GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(value.color(WHITE)).apply(builder -> {
    }));

    Args3<String, String, String> SET_DATA_SUCCESS = (key, value, target) -> prefixed(translatable()
            // 成功将 {2} 的数据键 {0} 设置为 {1}
            .key("floracore.command.generic.data.set").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(WHITE).append(text('\'')).append(formatColoredValue(value)).append(text('\'')), text().color(AQUA).append(text(target))).append(FULL_STOP));

    Args4<String, String, String, Duration> SET_DATA_TEMP_SUCCESS = (key, value, target, duration) -> prefixed(translatable()
            // 成功中将 {2} 的数据键 {0} 设置为 {1}, 有效期\: {3}
            .key("floracore.command.generic.data.set-temp").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(WHITE).append(text('\'')).append(formatColoredValue(value)).append(text('\'')), text().color(AQUA).append(text(target)), text().color(AQUA).append(DurationFormatter.LONG.format(duration))).append(FULL_STOP));

    Args2<String, String> DOESNT_HAVE_DATA = (target, key) -> prefixed(translatable()
            // {0} 没有设置数据键 {1}
            .key("floracore.command.generic.data.doesnt-have").color(RED).args(text().color(AQUA).append(text(target)), text().color(WHITE).append(text('\'')).append(text(key)).append(text('\''))).append(FULL_STOP));

    Args2<String, String> UNSET_DATA_SUCCESS = (key, target) -> prefixed(translatable()
            // 成功中为 {1} 取消设置数据键 {0}
            .key("floracore.command.generic.data.unset").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(AQUA).append(text(target))).append(FULL_STOP));

    Args2<String, DataType> DATA_CLEAR_SUCCESS = (target, type) -> prefixed(translatable()
            // {0} 的数据({1})已被清除
            .key("floracore.command.generic.data.clear").color(GREEN)
            // target
            .args(text().color(AQUA).append(text(target)),
                    // type
                    text().color(WHITE).append(OPEN_BRACKET).append(text(type == null ? "*" : type.getName())).append(CLOSE_BRACKET))
            // .
            .append(FULL_STOP));

    Args1<String> ILLEGAL_DATE_ERROR = invalid -> prefixed(translatable()
            // 无法解析日期 {0}
            .key("floracore.command.misc.date-parse-error").color(RED).args(text(invalid, DARK_RED)).append(FULL_STOP));

    Args0 PAST_DATE_ERROR = () -> prefixed(translatable()
            // 不能设置已经过去的日期\!
            .key("floracore.command.misc.date-in-past-error").color(RED));

    Args0 COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION = () -> prefixed(translatable()
            // 执行命令异常
            .key("floracore.command.misc.execute-command-exception").color(RED));

    Args0 COMMAND_TELEPORT_TOP = () -> prefixed(translatable()
            // 已传送到顶部
            .key("floracore.command.teleport.top").color(AQUA)).append(FULL_STOP);

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
                .key("floracore.command.misc.nick.book.start-page.accept.hover").color(WHITE).build());
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 1");
        return ARROW.color(BLACK).append(space()).append(translatable()
                // 我明白了,开始设置我的Nick
                .key("floracore.command.misc.nick.book.start-page.accept.text").color(BLACK).decoration(UNDERLINED, true)
                // hover
                .hoverEvent(hoverEvent)
                //click
                .clickEvent(clickEvent).build());
    };

    Args0 COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_1 = () -> translatable()
            // 让我们为你设置您的新昵称吧!
            .key("floracore.command.misc.nick.book.rank-page.line.1").color(BLACK).build();

    Args0 COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_2 = () -> translatable()
            // 首先,你需要选择你希望在Nick后显示为哪一个{0}
            .key("floracore.command.misc.nick.book.rank-page.line.2")
            // {0}
            .args(translatable("floracore.command.misc.nick.book.rank-page.rank").decoration(BOLD, true)).append(FULL_STOP).color(BLACK).build();

    Args2<String, String> COMMAND_MISC_NICK_BOOK_RANK_PAGE_RANK = (rankName, rank) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 2 " + rankName);
        Component r = formatColoredValue(rank);
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里,显示为 {0} 会员等级
                .key("floracore.command.misc.nick.book.rank-page.rank.hover").args(r.decoration(BOLD, true)).color(WHITE).build());
        return ARROW.color(BLACK).append(space()).append(r
                // hover
                .hoverEvent(hoverEvent)
                // click
                .clickEvent(clickEvent));
    };

    Args1<String> COMMAND_NICK_SETUP_RANK = (rank) -> {
        Component r = formatColoredValue(rank);
        return prefixed(translatable()
                // 你的昵称会员等级已设置为 {0} !
                .key("floracore.command.nick.setup.rank")
                // {0}
                .args(r).color(AQUA));
    };

    Args0 COMMAND_MISC_NICK_BOOK_SKIN_PAGE_LINE_1 = () -> translatable()
            // 芜湖!现在,你希望在Nick后使用哪种皮肤?
            .key("floracore.command.misc.nick.book.skin-page.line.1")
            // {0}
            .args(translatable("floracore.command.misc.nick.book.skin-page.skin").decoration(BOLD, true)).color(BLACK).build();

    Args1<String> COMMAND_MISC_NICK_BOOK_SKIN_PAGE_NORMAL = (rank) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 3 " + rank + " normal");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里以使用你自己的皮肤
                .key("floracore.command.misc.nick.book.skin-page.skin.normal.hover").color(WHITE).build());
        return ARROW.color(BLACK).append(space()).append(translatable()
                // 我自己的皮肤
                .key("floracore.command.misc.nick.book.skin-page.skin.normal").hoverEvent(hoverEvent).clickEvent(clickEvent).color(BLACK).build());
    };

    Args1<String> COMMAND_MISC_NICK_BOOK_SKIN_PAGE_STEVE_ALEX = (rank) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 3 " + rank + " steve-alex");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里以使用Steve/Alex的皮肤
                .key("floracore.command.misc.nick.book.skin-page.skin.steve-alex.hover").color(WHITE).build());
        return ARROW.color(BLACK).append(space()).append(translatable()
                // Steve/Alex的皮肤
                .key("floracore.command.misc.nick.book.skin-page.skin.steve-alex").hoverEvent(hoverEvent).clickEvent(clickEvent).color(BLACK).build());
    };

    Args1<String> COMMAND_MISC_NICK_BOOK_SKIN_PAGE_RANDOM = (rank) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 3 " + rank + " random");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里以使用随机皮肤
                .key("floracore.command.misc.nick.book.skin-page.skin.random.hover").color(WHITE).build());
        return ARROW.color(BLACK).append(space()).append(translatable()
                // 随机皮肤
                .key("floracore.command.misc.nick.book.skin-page.skin.random").hoverEvent(hoverEvent).clickEvent(clickEvent).color(BLACK).build());
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
                .key("floracore.command.misc.nick.book.skin-page.skin.reuse").hoverEvent(hoverEvent).clickEvent(clickEvent)
                // {0}
                .args(text(reuse)).color(BLACK).build());
    };

    Args0 COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_LINE_1 = () -> translatable()
            // 我们已经为你生成了一个随机的昵称:
            .key("floracore.command.misc.nick.book.random-page.line.1").color(BLACK).build();

    Args1<String> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_NAME = (name) -> translatable()
            // {0}
            .key("floracore.command.misc.nick.book.random-page.name").args(text(name).decoration(BOLD, true)).color(BLACK).build();

    Args3<String, String, String> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_USE_NAME = (rank, skin, name) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 5 " + rank + " " + skin + " random " + name);
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击以使用这个昵称
                .key("floracore.command.misc.nick.book.random-page.use-name.hover").color(WHITE).build());
        return space().append(space()).append(space()).append(translatable()
                // 使用这个昵称
                .key("floracore.command.misc.nick.book.random-page.use-name").hoverEvent(hoverEvent).clickEvent(clickEvent).decoration(UNDERLINED, true).color(GREEN).build());
    };

    Args2<String, String> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_TRY_AGAIN = (rank, skin) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin + " random");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里生成另一个昵称
                .key("floracore.command.misc.nick.book.random-page.try-again.hover").color(WHITE).build());
        return space().append(space()).append(space()).append(translatable()
                // 重新生成
                .key("floracore.command.misc.nick.book.random-page.try-again").hoverEvent(hoverEvent).clickEvent(clickEvent).decoration(UNDERLINED, true).color(RED).build());
    };

    Args2<String, String> COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_CUSTOM = (rank, skin) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin + " custom");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里以输入自定义昵称
                .key("floracore.command.misc.nick.book.random-page.custom.hover").color(WHITE).build());
        return translatable()
                // 或者使用自定义昵称
                .key("floracore.command.misc.nick.book.random-page.custom").hoverEvent(hoverEvent).clickEvent(clickEvent).decoration(UNDERLINED, true).color(BLACK).build();
    };

    Args0 COMMAND_NICK_SETUP_SKIN = () -> prefixed(translatable()
            // 你已拥有皮肤了!
            .key("floracore.command.nick.setup.skin").color(AQUA));

    Args0 COMMAND_UNNICK_SUCCESS = () -> prefixed(translatable()
            // 你的昵称已移除！
            .key("floracore.command.unnick.success").color(AQUA));

    Args0 COMMAND_UNNICK_NOT_IN = () -> prefixed(translatable()
            // 你当前未处于昵称状态！
            .key("floracore.command.unnick.not-in").color(RED));

    Args1<Integer> COMMAND_AIR_GET_SELF_MAX = value -> prefixed(translatable().key("floracore.command.air.get.self.max").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_GET_SELF_REMAINING = value -> prefixed(translatable().key("floracore.command.air.get.self.remaining").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_OTHER_MAX = (target, value) -> prefixed(translatable().key("floracore.command.air.get.other.max").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_OTHER_REMAINING = (target, value) -> prefixed(translatable().key("floracore.command.air.get.other.remaining").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_SELF_MAX = value -> prefixed(translatable().key("floracore.command.air.set.self.max").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_SELF_REMAINING = value -> prefixed(translatable().key("floracore.command.air.set.self.remaining").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_OTHER_MAX = (target, value) -> prefixed(translatable().key("floracore.command.air.set.other.max").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_OTHER_REMAINING = (target, value) -> prefixed(translatable().key("floracore.command.air.set.other.remaining").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_FROM_MAX = (from, value) -> prefixed(translatable().key("floracore.command.air.set.from.max").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_FROM_REMAINING = (from, value) -> prefixed(translatable().key("floracore.command.air.set.from.remaining").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args0 COMMAND_MISC_NICK_BOOK_NAME_PAGE_LINE_1 = () -> translatable()
            // 现在,请选择你要使用的{0}!
            .key("floracore.command.misc.nick.book.name-page.line.1")
            // {0}
            .args(translatable("floracore.command.misc.nick.book.name-page.name").decoration(BOLD, true)).color(BLACK).build();

    Args0 COMMAND_MISC_NICK_BOOK_RESET = () -> translatable()
            // 想要恢复平常状态,请输入{0}
            .key("floracore.command.misc.nick.book.reset")
            // {0}
            .args(text("/unnick").decoration(BOLD, true)).append(FULL_STOP).color(BLACK).build();

    Args2<String, String> COMMAND_MISC_NICK_BOOK_NAME_PAGE_RANDOM = (rank, skin) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin + " random");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里以使用随机昵称
                .key("floracore.command.misc.nick.book.name-page.name.random.hover").color(WHITE).build());
        return ARROW.color(BLACK).append(space()).append(translatable()
                // 使用随机昵称
                .key("floracore.command.misc.nick.book.name-page.name.random").hoverEvent(hoverEvent).clickEvent(clickEvent).color(BLACK).build());
    };

    Args2<String, String> COMMAND_MISC_NICK_BOOK_NAME_PAGE_CUSTOM = (rank, skin) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin + " custom");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 使用自定义昵称
                .key("floracore.command.misc.nick.book.name-page.name.custom.hover").color(WHITE).build());
        return ARROW.color(BLACK).append(space()).append(translatable()
                // 点击这里以使用自定义昵称
                .key("floracore.command.misc.nick.book.name-page.name.custom").hoverEvent(hoverEvent).clickEvent(clickEvent).color(BLACK).build());
    };

    Args3<String, String, String> COMMAND_MISC_NICK_BOOK_NAME_PAGE_REUSE = (rank, skin, reuse) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/book-nick 4 " + rank + " " + skin + " reuse");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 再次使用"{0}"
                .key("floracore.command.misc.nick.book.name-page.name.reuse.hover")
                // {0}
                .args(text(reuse)).color(WHITE).build());
        return ARROW.color(BLACK).append(space()).append(translatable()
                // 点击这里以再次使用"{0}"
                .key("floracore.command.misc.nick.book.name-page.name.reuse")
                // {0}
                .args(text(reuse)).hoverEvent(hoverEvent).clickEvent(clickEvent).color(BLACK).build());
    };

    Args0 COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_1 = () -> translatable()
            // 你已经完成了你的昵称的设置!
            .key("floracore.command.misc.nick.book.finish-page.line.1").color(BLACK).build();

    Args2<String, String> COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_2 = (rank, name) -> {
        Component r = formatColoredValue(rank + " " + name);
        return translatable()
                // 当你在游戏时,你的昵称将会变为{0}。你设置的昵称不会在大厅显示。
                .key("floracore.command.misc.nick.book.finish-page.line.2").color(BLACK)
                // {}
                .args(r).build();
    };

    Args0 COMMAND_MISC_NICK_ACTION_BAR = () -> translatable()
            // 当前已 {0}
            .key("floracore.command.misc.nick.action-bar").color(WHITE)
            // 匿名
            .args(translatable("floracore.command.misc.nick.action-bar.nick").color(RED)).build();

    Args1<String> COMMAND_MISC_NICK_RANK_UNKNOWN = (rank) -> prefixed(translatable()
            // {0} {1} 不存在!
            .key("floracore.command.misc.nick.rank.unknown")
            // {0}
            .args(translatable("floracore.command.misc.nick.book.rank-page.rank"), text(rank).color(DARK_RED).decoration(BOLD, true)).color(RED));

    Args1<String> COMMAND_MISC_NICK_RANK_NO_PERMISSION = (rank) -> prefixed(translatable()
            // 你没有 {0} {1} 的使用权限!
            .key("floracore.command.misc.nick.rank.no-permission")
            // {0}
            .args(translatable("floracore.command.misc.nick.book.rank-page.rank"), text(rank).color(DARK_RED).decoration(BOLD, true)).color(RED));

    Args2<String, String> COMMAND_REALNAME_SUCCESS = (name, realName) -> prefixed(translatable()
            // 玩家 {0} 的真实昵称为 {1}
            .key("floracore.command.realname.success")
            // {}
            .args(text(name).color(GREEN), text(realName).color(GREEN)).color(AQUA));


    Args1<Integer> COMMAND_AIR_GET_MAX_SELF = value -> prefixed(translatable().key("floracore.command.air.get.max.self").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_GET_REMAINING_SELF = value -> prefixed(translatable().key("floracore.command.air.get.remaining.self").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_MAX_OTHER = (target, value) -> prefixed(translatable().key("floracore.command.air.get.max.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_REMAINING_OTHER = (target, value) -> prefixed(translatable().key("floracore.command.air.get.remaining.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_MAX_SELF = value -> prefixed(translatable().key("floracore.command.air.set.max.self").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_REMAINING_SELF = value -> prefixed(translatable().key("floracore.command.air.set.remaining.self").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_MAX_OTHER = (target, value) -> prefixed(translatable().key("floracore.command.air.set.max.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_REMAINING_OTHER = (target, value) -> prefixed(translatable().key("floracore.command.air.set.remaining.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_MAX_FROM = (from, value) -> prefixed(translatable().key("floracore.command.air.set.max.from").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_REMAINING_FROM = (from, value) -> prefixed(translatable().key("floracore.command.air.set.remaining.from").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args0 COMMAND_ENDERCHEST_NOT_PLAYER = () -> prefixed(translatable().key("floracore.command.enderchest.not-player").color(RED));

    Args0 COMMAND_ENDERCHEST_OPEN_SELF = () -> prefixed(translatable().key("floracore.command.enderchest.open.self").color(AQUA));

    Args1<String> COMMAND_ENDERCHEST_OPEN_OTHER = target -> prefixed(translatable().key("floracore.command.enderchest.open.other").color(AQUA).args(text(target).color(GREEN)));

    Args2<String, String> COMMAND_ENDERCHEST_OPEN_FOR = (target, for_) -> prefixed(translatable().key("floracore.command.enderchest.open.for").color(AQUA).args(text(target).color(GREEN), text(for_).color(GREEN)));

    Args2<String, String> COMMAND_ENDERCHEST_OPEN_FROM = (from, target) -> prefixed(translatable().key("floracore.command.enderchest.open.from").color(AQUA).args(text(from).color(GREEN), text(target).color(GREEN)));

    Args0 COMMAND_ENDERCHEST_READONLY_TO = () -> prefixed(translatable().key("floracore.command.enderchest.readonly.to").color(YELLOW));

    Args0 COMMAND_ENDERCHEST_READONLY_FROM = () -> prefixed(translatable().key("floracore.command.enderchest.readonly.from").color(RED));

    Args1<Integer> COMMAND_FOOD_GET_SELF_NUTRITION = value -> prefixed(translatable().key("floracore.command.food.get.self.nutrition").color(AQUA).args(text(value).color(GREEN)));

    Args1<Float> COMMAND_FOOD_GET_SELF_SATURATION = value -> prefixed(translatable().key("floracore.command.food.get.self.saturation").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_GET_OTHER_NUTRITION = (target, value) -> prefixed(translatable().key("floracore.command.food.get.other.nutrition").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_GET_OTHER_SATURATION = (target, value) -> prefixed(translatable().key("floracore.command.food.get.other.saturation").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args1<Integer> COMMAND_FOOD_SET_SELF_NUTRITION = value -> prefixed(translatable().key("floracore.command.food.set.self.nutrition").color(AQUA).args(text(value).color(GREEN)));

    Args1<Float> COMMAND_FOOD_SET_SELF_SATURATION = value -> prefixed(translatable().key("floracore.command.food.set.self.saturation").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_SET_OTHER_NUTRITION = (target, value) -> prefixed(translatable().key("floracore.command.food.set.other.nutrition").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_SET_OTHER_SATURATION = (target, value) -> prefixed(translatable().key("floracore.command.food.set.other.saturation").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_SET_FROM_NUTRITION = (from, value) -> prefixed(translatable().key("floracore.command.food.set.from.nutrition").color(AQUA).args(text(from).color(GREEN), text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_SET_FROM_SATURATION = (from, value) -> prefixed(translatable().key("floracore.command.food.set.from.saturation").color(AQUA).args(text(from).color(GREEN), text(value).color(GREEN)));

    Args0 COMMAND_FOOD_SET_INVALIDVALUE = () -> prefixed(translatable().key("floracore.command.food.set.invalidvalue").color(RED));

    Args2<String, String> COMMAND_HASPERMISSION_YES = (target, permission) -> prefixed(translatable().key("floracore.command.haspermission.yes").color(AQUA).args(text(target).color(GREEN), text(permission)).color(GREEN));

    Args2<String, String> COMMAND_HASPERMISSION_NO = (target, permission) -> prefixed(translatable().key("floracore.command.haspermission.no").color(RED).args(text(target).color(GREEN), text(permission)).color(GREEN));

    Args0 COMMAND_FEED_SELF = () -> prefixed(translatable().key("floracore.command.feed.self").color(AQUA));

    Args1<String> COMMAND_FEED_OTHER = target -> prefixed(translatable().key("floracore.command.feed.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_FEED_FROM = from -> prefixed(translatable().key("floracore.command.feed.from").color(AQUA).args(text(from).color(GREEN)));

    Args0 COMMAND_HEAL_SELF = () -> prefixed(translatable().key("floracore.command.heal.self").color(AQUA));

    Args1<String> COMMAND_HEAL_OTHER = target -> prefixed(translatable().key("floracore.command.heal.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_HEAL_FROM = from -> prefixed(translatable().key("floracore.command.heal.from").color(AQUA).args(text(from).color(GREEN)));

    Args1<Integer> COMMAND_FIRETICK_SELF = time -> prefixed(translatable().key("floracore.command.firetick.self").color(AQUA).args(text(time).color(GREEN)));

    Args2<String, Integer> COMMAND_FIRETICK_OTHER = (target, time) -> prefixed(translatable().key("floracore.command.firetick.other").color(AQUA).args(text(target).color(GREEN), text(time).color(GREEN)));

    Args2<String, Integer> COMMAND_FIRETICK_FROM = (from, time) -> prefixed(translatable().key("floracore.command.firetick.from").color(AQUA).args(text(from).color(GREEN), text(time).color(GREEN)));

    Args1<Integer> COMMAND_OPLIST_HEADER = count -> prefixed(translatable().key("floracore.command.oplist.header").color(AQUA).args(text(count).color(GREEN)));

    Args0 COMMAND_OPLIST_HEADER_NONE = () -> prefixed(translatable().key("floracore.command.oplist.header.none").color(AQUA));

    Args3<String, UUID, Boolean> COMMAND_OPLIST_ENTRY = (name, uuid, online) -> prefixed(translatable().key("floracore.command.oplist.entry").color(AQUA).args(text(name).color(GREEN), text(uuid.toString()).color(GREEN), translatable(online ? "floracore.command.misc.online" : "floracore.command.misc.offline").color(online ? GREEN : RED)));

    Args1<Integer> COMMAND_PING_SELF = ping -> prefixed(translatable().key("floracore.command.ping.self").color(AQUA).args(text(ping).color(ping > 250 ? DARK_RED : ping > 200 ? RED : ping > 150 ? GOLD : ping > 100 ? YELLOW : ping > 50 ? GREEN : ping > 0 ? DARK_GREEN : WHITE)));

    Args2<String, Integer> COMMAND_PING_OTHER = (target, ping) -> prefixed(translatable().key("floracore.command.ping.other").color(AQUA).args(text(target).color(GREEN), text(ping).color(ping > 250 ? DARK_RED : ping > 200 ? RED : ping > 150 ? GOLD : ping > 100 ? YELLOW : ping > 50 ? GREEN : ping > 0 ? DARK_GREEN : WHITE)));

    Args1<Double> COMMAND_MAXHEALTH_GET_SELF = value -> prefixed(translatable().key("floracore.command.maxhealth.get.self").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_GET_OTHER = (target, value) -> prefixed(translatable().key("floracore.command.maxhealth.get.other").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args1<Double> COMMAND_MAXHEALTH_SET_SELF = value -> prefixed(translatable().key("floracore.command.maxhealth.set.self").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_SET_OTHER = (target, value) -> prefixed(translatable().key("floracore.command.maxhealth.set.other").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_SET_FROM = (from, value) -> prefixed(translatable().key("floracore.command.maxhealth.set.from").color(AQUA).args(text(from).color(GREEN), text(value).color(GREEN)));

    Args0 COMMAND_MISC_SPEED_FLY = () -> translatable("floracore.command.misc.speed.fly");
    Args0 COMMAND_MISC_SPEED_WALK = () -> translatable("floracore.command.misc.speed.walk");

    Args1<String> COMMAND_SPEED_NO_SUCH = (type) -> prefixed(translatable()
            // {0} 不是合法的速度类型
            .key("floracore.command.speed.nosuch")
            // {0}
            .args(text(type)).color(RED));

    Args1<String> COMMAND_MISC_INVALID_NUMBER = (number) -> prefixed(translatable()
            // {0} 不是有效的数字
            .key("floracore.command.misc.invalid-number")
            // {0}
            .args(text(number)).color(RED));

    Args3<String, Component, String> COMMAND_SPEED_OTHER = (sender, type, speed) -> prefixed(translatable()
            // {0} 将您的 {1} 速度设为 {2}
            .key("floracore.command.speed.other")
            // {}
            .args(text(sender).color(GREEN), type.color(YELLOW), text(speed).color(DARK_GREEN)).color(AQUA));

    Args3<String, Component, String> COMMAND_SPEED = (target, type, speed) -> prefixed(translatable()
            // {0} 将您的 {1} 速度设为 {2}
            .key("floracore.command.speed")
            // {}
            .args(text(target).color(GREEN), type.color(YELLOW), text(speed).color(DARK_GREEN)).color(AQUA));

    Args0 COMMAND_SUICIDE = () -> prefixed(translatable()
            // 你已自杀
            .key("floracore.command.suicide").color(AQUA));

    Args1<String> COMMAND_SUICIDE_BROADCAST = (target) -> prefixed(translatable()
            // {0} 自杀了!
            .key("floracore.command.suicide.broadcast")
            // {}
            .args(text(target).color(GREEN)).color(AQUA));

    Args0 COMMAND_ITEMFLAG_NOITEM_SELF = () -> prefixed(translatable().key("floracore.command.itemflag.noitem.self").color(RED));

    Args1<String> COMMAND_ITEMFLAG_NOITEM_OTHER = target -> prefixed(translatable().key("floracore.command.itemflag.noitem.other").color(RED).args(text(target).color(GREEN)));

    Args0 COMMAND_ITEMFLAG_UNSUPPORTEDITEM = () -> prefixed(translatable().key("floracore.command.itemflag.unsupporteditem").color(RED));

    Args1<String> COMMAND_ITEMFLAG_ALREADYHAS_SELF = flag -> prefixed(translatable().key("floracore.command.itemflag.alreadyhas.self").color(RED).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ALREADYHAS_OTHER = (target, flag) -> prefixed(translatable().key("floracore.command.itemflag.alreadyhas.other").color(RED).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_HASNO_SELF = flag -> prefixed(translatable().key("floracore.command.itemflag.hasno.self").color(RED).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_HASNO_OTHER = (target, flag) -> prefixed(translatable().key("floracore.command.itemflag.hasno.other").color(RED).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_ADD_SELF = flag -> prefixed(translatable().key("floracore.command.itemflag.add.self").color(AQUA).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ADD_OTHER = (target, flag) -> prefixed(translatable().key("floracore.command.itemflag.add.other").color(AQUA).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ADD_FROM = (from, flag) -> prefixed(translatable().key("floracore.command.itemflag.add.from").color(AQUA).args(text(from).color(GREEN), text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_REMOVE_SELF = flag -> prefixed(translatable().key("floracore.command.itemflag.remove.self").color(AQUA).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_REMOVE_OTHER = (target, flag) -> prefixed(translatable().key("floracore.command.itemflag.remove.other").color(AQUA).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_REMOVE_FROM = (from, flag) -> prefixed(translatable().key("floracore.command.itemflag.remove.from").color(AQUA).args(text(from).color(GREEN), text(flag).color(GREEN)));

    Args0 COMMAND_ITEMFLAG_CLEAR_SELF = () -> prefixed(translatable().key("floracore.command.itemflag.clear.self").color(AQUA));

    Args1<String> COMMAND_ITEMFLAG_CLEAR_OTHER = target -> prefixed(translatable().key("floracore.command.itemflag.clear.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_CLEAR_FROM = from -> prefixed(translatable().key("floracore.command.itemflag.clear.from").color(AQUA).args(text(from).color(GREEN)));

    Args0 COMMAND_ITEMNAME_NOITEM_SELF = () -> prefixed(translatable().key("floracore.command.itemname.noitem.self").color(RED));

    Args1<String> COMMAND_ITEMNAME_NOITEM_OTHER = player -> prefixed(translatable().key("floracore.command.itemname.noitem.other").color(RED).args(text(player).color(GREEN)));

    Args0 COMMAND_ITEMNAME_UNSUPPORTEDITEM = () -> prefixed(translatable().key("floracore.command.itemname.unsupporteditem").color(RED));

    Args1<String> COMMAND_ITEMNAME_SET_SELF = name -> prefixed(translatable().key("floracore.command.itemname.set.self").color(AQUA).args(text(name).color(GREEN)));

    Args2<String, String> COMMAND_ITEMNAME_SET_OTHER = (player, name) -> prefixed(translatable().key("floracore.command.itemname.set.other").color(AQUA).args(text(player).color(GREEN), text(name).color(GREEN)));

    Args2<String, String> COMMAND_ITEMNAME_SET_FROM = (from, name) -> prefixed(translatable().key("floracore.command.itemname.set.from").color(AQUA).args(text(from).color(GREEN), text(name).color(GREEN)));

    Args0 COMMAND_ITEMNAME_RESET_SELF = () -> prefixed(translatable().key("floracore.command.itemname.reset.self").color(AQUA));

    Args1<String> COMMAND_ITEMNAME_RESET_OTHER = player -> prefixed(translatable().key("floracore.command.itemname.reset.other").color(AQUA).args(text(player).color(GREEN)));

    Args1<String> COMMAND_ITEMNAME_RESET_FROM = from -> prefixed(translatable().key("floracore.command.itemname.reset.from").color(AQUA).args(text(from).color(GREEN)));

    Args1<String> COMMAND_GIVE_ITEM_NOSUCH = itemKey -> prefixed(translatable().key("floracore.command.give.item.nosuch").color(RED).args(text(itemKey).color(YELLOW)));

    Args1<String> COMMAND_GIVE_ITEM_NODATA = itemKey -> prefixed(translatable().key("floracore.command.give.item.nodata").color(RED).args(text(itemKey).color(YELLOW)));

    Args0 COMMAND_GIVE_ITEM_NBTSYTAXEXCEPTION = () -> prefixed(translatable().key("floracore.command.give.item.nbtsyntaxexception").color(RED));

    Args2<String, String> COMMAND_GIVE_ITEM_GIVEN = (item, player) -> prefixed(translatable().key("floracore.command.give.item.given").color(AQUA).args(
            text(item).color(WHITE),
            text(player).color(GREEN)
    ));

    Args1<String> COMMAND_BROADCAST = contents -> text().append(PREFIX_BROADCAST).append(space()).append(formatColoredValue(contents)).build();

    Args1<String> COMMAND_MISC_REPORT_NOTICE_ACCEPTED = target -> prefixed(translatable().key("floracore.command.misc.report.notice.accepted").color(AQUA).args(text(target).color(RED)));

    Args1<String> COMMAND_MISC_REPORT_NOTICE_PROCESSED = (target) -> prefixed(translatable().key("floracore.command.misc.report.notice.processed").color(AQUA).args(text(target).color(RED)));

    Args2<String, String> COMMAND_MISC_REPORT_NOTICE_STAFF_ACCEPTED = (reporter, target) -> prefixed(translatable().key("floracore.command.misc.report.notice.staff.accepted").color(AQUA).args(text(reporter).color(GREEN), text(target).color(RED)));

    Args2<String, String> COMMAND_MISC_REPORT_NOTICE_STAFF_PROCESSED = (reporter, target) -> prefixed(translatable().key("floracore.command.misc.report.notice.staff.processed").color(AQUA).args(text(reporter).color(GREEN), text(target).color(RED)));

    Args0 COMMAND_MISC_REPORT_THANKS = () -> prefixed(translatable().key("floracore.command.misc.report.thanks").color(AQUA));

    Args7<String, String, String, String, String, Boolean, Boolean> COMMAND_MISC_REPORT_BROADCAST = (player, target, playerServer, targetServer, reason, playerOnlineStatus, targetOnlineStatus) -> {
        Component infoLine = text()
                // 玩家 {0} 所在的服务器: {1} {2}
                .append(translatable().key("floracore.command.misc.report.broadcast.hover.line.1").color(AQUA)
                        // {}
                        .args(text(player).color(GREEN), text(playerServer).color(YELLOW),
                                OPEN_BRACKET.append(translatable(playerOnlineStatus ? "floracore.command.misc.online" : "floracore.command.misc.offline")).append(CLOSE_BRACKET).color(playerOnlineStatus ? GREEN : RED))).append(newline())
                .append(translatable().key("floracore.command.misc.report.broadcast.hover.line.1").color(AQUA)
                        .args(text(target).color(GREEN), text(targetServer).color(YELLOW),
                                OPEN_BRACKET.append(translatable(targetOnlineStatus ? "floracore.command.misc.online" : "floracore.command.misc.offline")).append(CLOSE_BRACKET).color(targetOnlineStatus ? GREEN : RED)))
                .build();
        if (targetOnlineStatus) {
            infoLine = infoLine.append(newline()).append(ARROW).append(space()).append(translatable().key("floracore.command.misc.check-tp").color(YELLOW).decoration(UNDERLINED, true));
        }
        HoverEvent<Component> hoverEvent = HoverEvent.showText(infoLine);
        ClickEvent clickEvent = ClickEvent.runCommand("/report-tp " + target);
        Component i = prefixed(translatable().key("floracore.command.misc.report.broadcast").color(AQUA)
                // {}
                .args(text(player).color(GREEN), text(target).color(RED), text(reason).color(YELLOW)))
                // hoverEvent
                .hoverEvent(hoverEvent);
        if (targetOnlineStatus) {
            i = i.clickEvent(clickEvent);
        }
        // 玩家 {0} 以 {2} 的理由举报了玩家 {1}
        return i;
    };

    Args0 CHECK_TP = () -> translatable("floracore.command.misc.check-tp", YELLOW);

    Args2<String, String> COMMAND_REPORT_SUCCESS = (target, reason) -> prefixed(translatable()
            // 你以"{1}"的理由举报了玩家 {0} ,请您耐心等待工作人员处理!
            .key("floracore.command.report.success").color(AQUA).args(text(target, RED), text(reason, YELLOW)));

    Args0 COMMAND_REPORT_REPEAT = () -> prefixed(translatable()
            // 你已经举报过这名玩家了!
            .key("floracore.command.report.repeat").color(RED));

    Args1<String> COMMAND_REPORT_TP_SUCCESS = id -> prefixed(translatable()
            // 已将你传送至玩家 {0} 的旁边!
            .key("floracore.command.report.tp.success").color(AQUA).args(text(id, GREEN)));

    Args0 COMMAND_REPORT_TP_TRANSMITTING = () -> prefixed(translatable()
            // 传送中...
            .key("floracore.command.report.tp.transmitting").color(AQUA));

    Args0 COMMAND_REPORT_NOT_PERMISSION = () -> prefixed(translatable()
            // 你不能举报这名玩家!
            .key("floracore.command.report.no-permission").color(RED));

    Args0 COMMAND_REPORT_SELF = () -> prefixed(translatable()
            // 你不能举报你自己!
            .key("floracore.command.report.self").color(RED));

    Args0 COMMAND_REPORT_ABNORMAL = () -> prefixed(translatable()
            // 这名玩家的数据异常!
            .key("floracore.command.report.abnormal").color(RED));

    Args1<Integer> COMMAND_REPORTS_GUI_PAGE = (page) -> translatable()
            // 第 {0} 页
            .key("floracore.command.misc.reports.gui.page").args(text(page, GREEN)).color(AQUA).build();

    Args1<Integer> COMMAND_REPORTS_GUI_MAIN_REPORT_TITLE = (page) -> translatable()
            // 举报 {0}
            .key("floracore.command.misc.reports.gui.main.report.title").args(text("#" + page, GRAY)).color(RED).build();

    Args1<Integer> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_TITLE = (page) -> translatable()
            // 聊天 {0}
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.title").args(text("#" + page, GRAY)).color(RED).build();

    Args1<Component> COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS = (status) -> translatable()
            // 状态: {0}
            .key("floracore.command.misc.reports.gui.main.report.status").args(status).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_PLAYER = (player) -> translatable()
            // 归属人: {0}
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.player").args(text(player, GREEN)).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_TIME = (time) -> translatable()
            // 日期: {0}
            .key("floracore.command.misc.reports.gui.main.report.report-time").args(text(time, YELLOW)).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_START_TIME = (time) -> translatable()
            // 开始时间: {0}
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.start-time").args(text(time, YELLOW)).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_START_TIME_BOOK = (time) -> translatable()
            // 开始时间: {0}
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.start-time").args(text(time, DARK_GREEN)).color(BLACK).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_END_TIME = (time) -> translatable()
            // 结束时间: {0}
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.end-time").args(text(time, YELLOW)).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_END_TIME_BOOK = (time) -> translatable()
            // 结束时间: {0}
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.end-time").args(text(time, DARK_GREEN)).color(BLACK).build();

    Args1<Integer> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_1 = (amounts) -> translatable()
            // 共 {0} 条聊天记录
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.1").args(text(amounts).decoration(BOLD, true).decoration(UNDERLINED, true)).color(GOLD).build();

    Args0 COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_2 = () -> translatable()
            // 翻页查看
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.2").color(RED).decoration(BOLD, true).decoration(UNDERLINED, true).build();

    Args1<UUID> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_3 = (uuid) -> {
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                // 点击这里以返回
                .key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.3.hover").color(WHITE).build());
        ClickEvent clickEvent = ClickEvent.runCommand("/rcs " + uuid.toString());
        return translatable()
                // 返回至聊天记录
                .key("floracore.command.misc.reports.gui.main.report.chats.chat.book.main.line.3").clickEvent(clickEvent).hoverEvent(hoverEvent).color(BLACK).decoration(BOLD, true).decoration(UNDERLINED, true).build();
    };

    Args4<String, String, String, Boolean> COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_CHAT = (time, player, chat, target) -> translatable()
            // {0} {1} : {2}
            .key("floracore.command.misc.reports.gui.main.report.chats.chat.book.chat")
            .args(text(time), text(player).decoration(BOLD, true), text(chat, BLACK).decoration(UNDERLINED, true)).color(target ? RED : BLACK).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORTER = (reporters) -> translatable()
            // 举报者: {0}
            .key("floracore.command.misc.reports.gui.main.report.reporter").args(text(reporters, GREEN)).color(GRAY).build();

    Args0 COMMAND_REPORTS_GUI_MAIN_REPORTER_TITLE = () -> translatable()
            // 举报者列表
            .key("floracore.command.misc.reports.gui.main.report.reporter.title").color(GOLD).build();

    Args0 COMMAND_REPORTS_GUI_MAIN_CHATS_TITLE = () -> translatable()
            // 聊天列表
            .key("floracore.command.misc.reports.gui.main.report.chats.title").color(GOLD).build();

    Args2<String, Boolean> COMMAND_REPORTS_GUI_MAIN_REPORTED = (reported, online) -> translatable()
            // 被举报者: {0} {1}
            .key("floracore.command.misc.reports.gui.main.report.reported")
            .args(text(reported, RED),
                    OPEN_BRACKET.append(translatable(online ? "floracore.command.misc.online" : "floracore.command.misc.offline")).append(CLOSE_BRACKET).color(online ? GREEN : RED)).color(GRAY).build();

    Args2<String, Boolean> COMMAND_REPORTS_GUI_MAIN_REPORTER_DETAILED = (reported, online) -> translatable()
            // 举报者: {0} {1}
            .key("floracore.command.misc.reports.gui.main.report.reporter.detailed")
            .args(text(reported, RED),
                    OPEN_BRACKET.append(translatable(online ? "floracore.command.misc.online" : "floracore.command.misc.offline")).append(CLOSE_BRACKET).color(online ? GREEN : RED)).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REASON = (reason) -> translatable()
            // 原因: {0}
            .key("floracore.command.misc.reports.gui.main.report.reason").args(text(reason, AQUA)).color(GRAY).build();

    Args0 COMMAND_REPORTS_CLICK_TO_LOOK = () -> translatable()
            // 点击查看详情!
            .key("floracore.command.misc.report.click-to-look").color(YELLOW).build();

    Args0 COMMAND_REPORTS_STATUS_WAITING = () -> translatable()
            // 等待中
            .key("floracore.command.misc.reports.status.waiting").color(GREEN).build();

    Args0 COMMAND_REPORTS_STATUS_ACCEPTED = () -> translatable()
            // 受理中
            .key("floracore.command.misc.reports.status.accepted").color(YELLOW).build();

    Args0 COMMAND_REPORTS_STATUS_ENDED = () -> translatable()
            // 已完成
            .key("floracore.command.misc.reports.status.ended").color(RED).build();

    Args0 COMMAND_REPORTS_GUI_MAIN_TITLE = () -> translatable()
            // 举报列表
            .key("floracore.command.misc.reports.gui.main.title").color(GOLD).build();

    Args0 COMMAND_REPORTS_GUI_REPORT_TITLE = () -> translatable()
            // 举报
            .key("floracore.command.misc.reports.gui.report.title").color(GOLD).build();

    Args0 COMMAND_REPORTS_GUI_REPORT_CHAT = () -> translatable()
            // 聊天记录
            .key("floracore.command.misc.reports.gui.report.chat").color(GRAY).build();

    Args0 COMMAND_REPORTS_GUI_REPORT_ACCEPTED = () -> translatable()
            // 受理此举报
            .key("floracore.command.misc.reports.gui.report.accepted").color(GREEN).build();

    Args0 COMMAND_REPORTS_GUI_REPORT_END = () -> translatable()
            // 处理此举报
            .key("floracore.command.misc.reports.gui.report.end").color(AQUA).build();

    Args0 COMMAND_REPORTS_GUI_REPORT_ENDED = () -> translatable()
            // 此举报已处理
            .key("floracore.command.misc.reports.gui.report.ended").color(RED).build();

    Args0 COMMAND_MISC_GUI_CLOSE = () -> translatable()
            // 关闭
            .key("floracore.command.misc.gui.close").color(RED).build();

    Args0 COMMAND_MISC_CHAT = () -> translatable()
            // 聊天记录
            .key("floracore.command.misc.chat").decoration(BOLD, true).decoration(UNDERLINED, true).color(BLACK).build();

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

    Args0 COMMAND_LANGUAGE_TITLE = () -> translatable()
            // 切换你的显示语言
            .key("floracore.command.misc.language.title").color(BLACK).build();

    Args1<String> COMMAND_LANGUAGE_CHANGE = (language) -> translatable()
            // 点击切换为 {0} !
            .key("floracore.command.misc.language.change").args(text(language).decoration(BOLD, true)).color(YELLOW).build();

    Args1<String> COMMAND_LANGUAGE_CHANGE_SUCCESS = (language) -> translatable()
            // 你已成功将你的显示语言更改为 {0} !
            .key("floracore.command.language.change.success").args(text(language, GREEN)).color(AQUA).build();

    static TextComponent prefixed(ComponentLike component) {
        return text().append(PREFIX_COMPONENT).append(space()).append(component).build();
    }

    static Component formatStringList(Collection<String> strings) {
        Iterator<String> it = strings.iterator();
        if (!it.hasNext()) {
            return translatable("floracore.command.misc.none", AQUA);
        }

        TextComponent.Builder builder = text().color(DARK_AQUA).content(it.next());

        while (it.hasNext()) {
            builder.append(text(", ", GRAY));
            builder.append(text(it.next()));
        }

        return builder.build();
    }

    static Component formatBoolean(boolean bool) {
        return bool ? text("true", GREEN) : text("false", RED);
    }

    static Component formatColoredValue(String value) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(value).toBuilder().build();
    }

    interface Args0 {
        Component build();

        default void send(Sender sender) {
            sender.sendMessage(build());
        }
    }

    interface Args1<A0> {
        Component build(A0 arg0);

        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }
    }

    interface Args2<A0, A1> {
        Component build(A0 arg0, A1 arg1);

        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }
    }

    interface Args3<A0, A1, A2> {
        Component build(A0 arg0, A1 arg1, A2 arg2);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2) {
            sender.sendMessage(build(arg0, arg1, arg2));
        }
    }

    interface Args4<A0, A1, A2, A3> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3));
        }
    }

    interface Args5<A0, A1, A2, A3, A4> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4));
        }
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5));
        }
    }

    interface Args7<A0, A1, A2, A3, A4, A5, A6> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 args6);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 args6) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5, args6));
        }
    }
}
