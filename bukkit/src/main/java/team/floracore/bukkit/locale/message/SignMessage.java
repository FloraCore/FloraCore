package team.floracore.bukkit.locale.message;

import team.floracore.common.locale.message.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface SignMessage extends AbstractMessage {
    Args0 COMMAND_MISC_NICK_SIGN_LINE_2 = () -> translatable()
            // ^^^^^^^^^^^^^^^
            .key("floracore.command.misc.nick.sign.line.2").color(BLACK).build();
    Args0 COMMAND_MISC_NICK_SIGN_LINE_3 = () -> translatable()
            // Enter your
            .key("floracore.command.misc.nick.sign.line.3").color(BLACK).build();
    Args0 COMMAND_MISC_NICK_SIGN_LINE_4 = () -> translatable()
            // Nickname here
            .key("floracore.command.misc.nick.sign.line.4").color(BLACK).build();
}
