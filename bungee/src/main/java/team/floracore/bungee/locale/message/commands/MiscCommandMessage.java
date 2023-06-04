package team.floracore.bungee.locale.message.commands;

import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import team.floracore.common.locale.message.AbstractMessage;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public interface MiscCommandMessage extends AbstractMessage {
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
