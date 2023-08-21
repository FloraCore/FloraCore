package team.floracore.bungee.locale.message.commands;

import net.kyori.adventure.text.Component;
import team.floracore.common.locale.message.AbstractMessage;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface MiscCommandMessage extends AbstractMessage {
    Args3<String, Component, Boolean> SET_SERVER_DATA =
            (server, type, value) -> AbstractMessage.prefixed(
                    translatable()
                            // 成功将 {0} 的 {1} 设置为 {2}
                            .key("floracore.command.server.data.set")
                            .color(AQUA)
                            .args(text(server, GREEN), type,
                                    translatable(value ?
                                            "floracore.command.misc.on" :
                                            "floracore.command.misc.off")
                                            .color(value ? GREEN : RED))
                            .append(FULL_STOP));
}
