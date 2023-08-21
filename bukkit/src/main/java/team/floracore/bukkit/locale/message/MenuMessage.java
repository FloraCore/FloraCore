package team.floracore.bukkit.locale.message;

import net.kyori.adventure.text.Component;
import team.floracore.common.locale.message.AbstractMessage;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public interface MenuMessage extends AbstractMessage {
    Args1<Integer> COMMAND_REPORTS_GUI_PAGE = (page) -> translatable()
            // 第 {0} 页
            .key("floracore.command.misc.reports.gui.page").args(text(page, GREEN)).color(AQUA).build();

    Args1<Integer> COMMAND_REPORTS_GUI_MAIN_REPORT_TITLE = (page) -> translatable()
            // 举报 {0}
            .key("floracore.command.misc.reports.gui.main.report.title")
            .args(text("#" + page, GRAY))
            .color(RED)
            .build();

    Args1<Component> COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS = (status) -> translatable()
            // 状态: {0}
            .key("floracore.command.misc.reports.gui.main.report.status").args(status).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORT_TIME = (time) -> translatable()
            // 日期: {0}
            .key("floracore.command.misc.reports.gui.main.report.report-time")
            .args(text(time, YELLOW))
            .color(GRAY)
            .build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REPORTER = (reporters) -> translatable()
            // 举报者: {0}
            .key("floracore.command.misc.reports.gui.main.report.reporter")
            .args(text(reporters, GREEN))
            .color(GRAY)
            .build();

    Args0 COMMAND_REPORTS_GUI_MAIN_REPORTER_TITLE = () -> translatable()
            // 举报者列表
            .key("floracore.command.misc.reports.gui.main.report.reporter.title").color(GOLD).build();

    Args2<String, Boolean> COMMAND_REPORTS_GUI_MAIN_REPORTED = (reported, online) -> translatable()
            // 被举报者: {0} {1}
            .key("floracore.command.misc.reports.gui.main.report.reported")
            .args(text(reported, RED),
                    OPEN_BRACKET.append(translatable(online ?
                                    "floracore.command.misc.online" :
                                    "floracore.command.misc.offline"))
                            .append(CLOSE_BRACKET)
                            .color(online ? GREEN : RED)).color(GRAY).build();

    Args2<String, Boolean> COMMAND_REPORTS_GUI_MAIN_REPORTER_DETAILED = (reported, online) -> translatable()
            // 举报者: {0} {1}
            .key("floracore.command.misc.reports.gui.main.report.reporter.detailed")
            .args(text(reported, RED),
                    OPEN_BRACKET.append(translatable(online ?
                                    "floracore.command.misc.online" :
                                    "floracore.command.misc.offline"))
                            .append(CLOSE_BRACKET)
                            .color(online ? GREEN : RED)).color(GRAY).build();

    Args1<String> COMMAND_REPORTS_GUI_MAIN_REASON = (reason) -> translatable()
            // 原因: {0}
            .key("floracore.command.misc.reports.gui.main.report.reason")
            .args(text(reason, AQUA)).color(GRAY).build();

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

    Args0 COMMAND_REPORTS_GUI_PROCESSED = () -> OPEN_BRACKET.append(translatable()
                    // (已处理)
                    .key("floracore.command.misc.reports.gui.processed"))
            .append(CLOSE_BRACKET)
            .color(DARK_AQUA);

    Args0 COMMAND_REPORTS_GUI_MAIN_TITLE = () -> translatable()
            // 举报列表
            .key("floracore.command.misc.reports.gui.main.title").color(GOLD).build();

    Args0 COMMAND_REPORTS_GUI_MAIN_PROCESSED = () -> translatable()
            // 查看已处理的举报列表
            .key("floracore.command.misc.reports.gui.main.processed").color(YELLOW).build();

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
