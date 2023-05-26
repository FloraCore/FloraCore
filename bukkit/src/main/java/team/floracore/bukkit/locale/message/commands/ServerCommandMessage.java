package team.floracore.bukkit.locale.message.commands;

import team.floracore.common.locale.message.*;

import static net.kyori.adventure.text.Component.*;

public interface ServerCommandMessage extends AbstractMessage {
    Args1<String> COMMAND_BROADCAST = contents -> text().append(MiscMessage.PREFIX_BROADCAST)
            .append(space())
            .append(AbstractMessage.formatColoredValue(contents))
            .build();

}
