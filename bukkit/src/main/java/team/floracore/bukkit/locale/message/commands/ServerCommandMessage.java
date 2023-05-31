package team.floracore.bukkit.locale.message.commands;

import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

public interface ServerCommandMessage extends AbstractMessage {
    Args1<String> COMMAND_BROADCAST = contents -> text().append(MiscMessage.PREFIX_BROADCAST)
                                                        .append(space())
                                                        .append(AbstractMessage.formatColoredValue(contents))
                                                        .build();

}
