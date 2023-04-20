package team.floracore.common.locale;

import net.kyori.adventure.text.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.sender.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

// @formatter:off
public interface Message {
    TextComponent OPEN_BRACKET = Component.text('(');
    TextComponent CLOSE_BRACKET = Component.text(')');
    TextComponent FULL_STOP = Component.text('.');

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
                .append(text("Fl", AQUA))
                .append(text("ora", DARK_GREEN))
                .append(text("Core", GREEN))
                .append(space())
                .append(text("v" + bootstrap.getVersion(), AQUA))
                .append(space())
                .append(text("is Running",DARK_GRAY))
                .build();

        JoinConfiguration joinConfig = JoinConfiguration.builder()
                .separator(newline())
                .build();

        return join(joinConfig,
                text().append(infoLine1).build()
        );
    };
    Args1<String> TRANSLATIONS_INSTALLING_SPECIFIC = name -> prefixed(translatable()
            // "&aInstalling language {}..."
            .key("floracore.command.translations.installing-specific")
            .color(GREEN)
            .args(text(name))
    );
    Args1<String> TRANSLATIONS_DOWNLOAD_ERROR = name -> prefixed(text()
            // "&cUnable download translation for {}. Check the console for errors."
            .color(RED)
            .append(translatable("floracore.command.translations.download-error", text(name, DARK_RED)))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("floracore.command.misc.check-console-for-errors"))
            .append(FULL_STOP)
    );

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
