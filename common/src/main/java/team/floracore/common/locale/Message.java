package team.floracore.common.locale;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.*;
import net.kyori.adventure.text.serializer.legacy.*;
import org.bukkit.*;
import team.floracore.api.data.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

import java.time.*;
import java.util.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public interface Message {
    TextComponent OPEN_BRACKET = Component.text('(');
    TextComponent CLOSE_BRACKET = Component.text(')');
    TextComponent FULL_STOP = Component.text('.');

    Component PREFIX_COMPONENT = text()
            // [FC]
            .color(GRAY).append(text('[')).append(text().decoration(BOLD, true).append(text('F', AQUA)).append(text('C', YELLOW))).append(text(']')).build();
    Args1<FloraCoreBootstrap> STARTUP_BANNER = bootstrap -> {
        // FloraCore v{} is Running.
        Component infoLine1 = text()
                // FloraCore
                .append(text("Fl", AQUA)).append(text("ora", DARK_GREEN)).append(text("Core", YELLOW)).append(space())
                // v{}
                .append(text("v" + bootstrap.getVersion(), AQUA)).append(space())
                // Running
                .append(text("is Running", DARK_GRAY)).build();

        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();

        return join(joinConfig, text().append(infoLine1).build());
    };

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
            .key("floracore.commandsystem.no-permission-subcommands").color(DARK_AQUA).append(FULL_STOP));

    Args0 COMMAND_NO_PERMISSION = () -> prefixed(translatable()
            // 您没有使用此命令的权限！
            .key("floracore.commandsystem.no-permission").color(RED));

    Args1<String> COMMAND_INVALID_COMMAND_SYNTAX = correctSyntax -> prefixed(text()
            // 命令语法错误，您可能想要输入：{0}
            .color(RED).append(translatable("floracore.commandsystem.invalid-command-syntax", text(correctSyntax, DARK_AQUA))).append(FULL_STOP));

    Args2<String, String> COMMAND_INVALID_COMMAND_SENDER = (commandSender, requiredSender) -> prefixed(text()
            // {0} 不允许执行该命令，必须由 {1} 执行
            .color(RED).append(translatable("floracore.commandsystem.invalid-command-sender", text(commandSender), text(requiredSender))).append(FULL_STOP));

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
        return LegacyComponentSerializer.legacyAmpersand().deserialize(value).toBuilder().hoverEvent(HoverEvent.showText(text(value, WHITE))).build();
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
}
