package team.floracore.common.locale.message;

import net.kyori.adventure.text.*;

import java.util.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface CommonCommandMessage extends AbstractMessage {
    Args0 RELOAD_CONFIG_SUCCESS = () -> AbstractMessage.prefixed(translatable()
            // 已重新加载配置文件
            .key("floracore.command.reload-config.success").color(GREEN).append(FULL_STOP).append(space())
            // 某些选项仅在服务器重新启动后才应用
            .append(text().color(GRAY).append(OPEN_BRACKET).append(translatable("floracore.command.reload-config.restart-note")).append(CLOSE_BRACKET)));

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
    Args0 TRANSLATIONS_INSTALLING = () -> AbstractMessage.prefixed(translatable()
            // 正在安装翻译, 请稍候...
            .key("floracore.command.translations.installing").color(AQUA));

    Args0 TRANSLATIONS_INSTALL_COMPLETE = () -> AbstractMessage.prefixed(translatable()
            // 安装已完成
            .key("floracore.command.translations.install-complete").color(AQUA).append(FULL_STOP));

    Args1<String> DATA_NONE = target -> AbstractMessage.prefixed(translatable()
            // {0} 无记录的数据
            .key("floracore.command.generic.data.none").color(AQUA).args(text(target)).append(FULL_STOP));

    Args1<String> DATA_HEADER = target -> AbstractMessage.prefixed(translatable()
            // {0} 的数据信息:
            .key("floracore.command.generic.data.info.title").color(AQUA).args(text(target)));

    Args2<Component, String> SERVER_DATA_ENTRY = (key, value) -> AbstractMessage.prefixed(text().append(key.color(GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(text(value, WHITE)).apply(builder -> {
    }));
    Args2<Component, Component> SERVER_DATA_ENTRY_1 = (key, value) -> AbstractMessage.prefixed(text().append(key.color(GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(value.color(WHITE)).apply(builder -> {
    }));
}
