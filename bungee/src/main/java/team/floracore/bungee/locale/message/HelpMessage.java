package team.floracore.bungee.locale.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface HelpMessage extends AbstractMessage {
    Args1<String> HELP_DESCRIPTION = (path) -> translatable().key(path).color(AQUA).build();

    Args0 HELP_PARTY_TITLE = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component component = translatable()
                // 组队命令
                .key("floracore.command.description.party-commands").color(GREEN).build();
        Component description = translatable()
                // 组队是一个社交系统。玩家可以与其他玩家一起游玩
                .key("floracore.command.description.party").color(GRAY).build();
        Component i = HORIZONTAL_LINE.color(GRAY);
        return join(joinConfig,
                    MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                    component.append(space()).append(i).append(space()).append(description),
                    space());
    };

    Args2<String, String> HELP_PARTY_SUB_COMMAND = (cmd, path) -> {
        Component component = text("/" + cmd).color(YELLOW);
        Component description = HELP_DESCRIPTION.build(path);
        Component i = HORIZONTAL_LINE.color(GRAY);
        ClickEvent clickEvent = ClickEvent.suggestCommand("/" + cmd);
        HoverEvent<Component> hoverEvent = HoverEvent.showText(translatable()
                                                                       // Click to put the command in chat
                                                                       .key("floracore.command.misc.click.suggest-hover")
                                                                       .color(WHITE)
                                                                       .build());
        return text().append(component)
                     .append(space())
                     .append(i)
                     .append(space())
                     .append(description)
                     .clickEvent(clickEvent)
                     .hoverEvent(hoverEvent)
                     .build();
    };
}
