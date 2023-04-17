package team.floracore.common.locale;

import net.kyori.adventure.text.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.sender.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

// @formatter:off
public interface Message {
    Component PREFIX_COMPONENT = text()
            .color(GRAY)
            .append(text('['))
            .append(text()
                    .decoration(BOLD, true)
                    .append(text('F', AQUA))
                    .append(text('C', DARK_AQUA))
            )
            .append(text(']'))
            .build();
    Args1<FloraCoreBootstrap> STARTUP_BANNER = bootstrap -> {
        Component infoLine1 = text()
                .append(text("FloraCore", DARK_GREEN))
                .append(space())
                .append(text("v" + bootstrap.getVersion(), AQUA))
                .build();

        Component infoLine2 = text()
                .color(DARK_GRAY)
                .append(text("Running"))
                .build();

        JoinConfiguration joinConfig = JoinConfiguration.builder()
                .separator(newline())
                .build();

        return join(joinConfig,
                text()
                        .append(text("       ", AQUA))
                        .append(text(" __    ", DARK_AQUA))
                        .build(),
                text()
                        .append(text("  |    ", AQUA))
                        .append(text("|__)   ", DARK_AQUA))
                        .append(infoLine1)
                        .build(),
                text()
                        .append(text("  |___ ", AQUA))
                        .append(text("|      ", DARK_AQUA))
                        .append(infoLine2)
                        .build(),
                empty()
        );
    };

    static TextComponent prefixed(ComponentLike component) {
        return text()
                .append(PREFIX_COMPONENT)
                .append(space())
                .append(component)
                .build();
    }

    interface Args1<A0> {
        Component build(A0 arg0);

        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }
    }
}
