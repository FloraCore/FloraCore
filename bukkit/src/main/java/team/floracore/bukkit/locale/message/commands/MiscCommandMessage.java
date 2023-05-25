package team.floracore.bukkit.locale.message.commands;

import org.floracore.api.data.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.util.*;

import java.time.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface MiscCommandMessage extends AbstractMessage {
    Args4<String, String, String, Long> DATA_ENTRY = (type, key, value, expiry) -> {
        Instant instant = Instant.ofEpochMilli(expiry);
        Instant now = Instant.now();
        Duration timeElapsed = Duration.between(now, instant);
        return AbstractMessage.prefixed(text().append(text(type, GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(text(key, AQUA)).append(text(" - ", WHITE)).append(text().color(WHITE).append(text('\'')).append(text(value)).append(text('\''))).apply(builder -> {
            if (expiry > 0) {
                builder.append(space());
                builder.append(text().color(DARK_GRAY).append(OPEN_BRACKET).append(translatable()
                        // 过期时间
                        .key("floracore.command.generic.info.expires-in").color(GRAY).append(space()).append(text().color(AQUA).append(DurationFormatter.CONCISE.format(timeElapsed)))).append(CLOSE_BRACKET));
            }
        }));
    };

    Args3<String, String, String> SET_DATA_SUCCESS = (key, value, target) -> AbstractMessage.prefixed(translatable()
            // 成功将 {2} 的数据键 {0} 设置为 {1}
            .key("floracore.command.generic.data.set").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(WHITE).append(text('\'')).append(AbstractMessage.formatColoredValue(value)).append(text('\'')), text().color(AQUA).append(text(target))).append(FULL_STOP));

    Args4<String, String, String, Duration> SET_DATA_TEMP_SUCCESS = (key, value, target, duration) -> AbstractMessage.prefixed(translatable()
            // 成功中将 {2} 的数据键 {0} 设置为 {1}, 有效期\: {3}
            .key("floracore.command.generic.data.set-temp").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(WHITE).append(text('\'')).append(AbstractMessage.formatColoredValue(value)).append(text('\'')), text().color(AQUA).append(text(target)), text().color(AQUA).append(DurationFormatter.LONG.format(duration))).append(FULL_STOP));

    Args2<String, String> DOESNT_HAVE_DATA = (target, key) -> AbstractMessage.prefixed(translatable()
            // {0} 没有设置数据键 {1}
            .key("floracore.command.generic.data.doesnt-have").color(RED).args(text().color(AQUA).append(text(target)), text().color(WHITE).append(text('\'')).append(text(key)).append(text('\''))).append(FULL_STOP));

    Args2<String, String> UNSET_DATA_SUCCESS = (key, target) -> AbstractMessage.prefixed(translatable()
            // 成功中为 {1} 取消设置数据键 {0}
            .key("floracore.command.generic.data.unset").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(AQUA).append(text(target))).append(FULL_STOP));

    Args2<String, DataType> DATA_CLEAR_SUCCESS = (target, type) -> AbstractMessage.prefixed(translatable()
            // {0} 的数据({1})已被清除
            .key("floracore.command.generic.data.clear").color(GREEN)
            // target
            .args(text().color(AQUA).append(text(target)),
                    // type
                    text().color(WHITE).append(OPEN_BRACKET).append(text(type == null ? "*" : type.getName())).append(CLOSE_BRACKET))
            // .
            .append(FULL_STOP));

    Args1<String> COMMAND_LANGUAGE_CHANGE_SUCCESS = (language) -> translatable()
            // 你已成功将你的显示语言更改为 {0} !
            .key("floracore.command.language.change.success").args(text(language, GREEN)).color(AQUA).build();
}
