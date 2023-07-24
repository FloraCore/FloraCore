package team.floracore.bukkit.locale.message.commands;

import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface ServerCommandMessage extends AbstractMessage {
    Args1<String> COMMAND_BROADCAST = contents -> text().append(MiscMessage.PREFIX_BROADCAST)
            .append(space())
            .append(AbstractMessage.formatColoredValue(contents))
            .build();

    Args1<String> COMMAND_BROADCAST_WITHOUT_PREFIX = contents -> text()
            .append(AbstractMessage.formatColoredValue(contents))
            .build();

    Args2<String, String> COMMAND_BUNGEE_COMMAND = (sender, command) -> AbstractMessage.prefixed(translatable()
            // {0} 成功执行了命令 {1}
            .key("floracore.command.bungee.command")
            .args(text(sender, GREEN), text(command, YELLOW)).color(AQUA));

}
