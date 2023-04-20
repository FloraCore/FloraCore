package team.floracore.common.locale;

import net.kyori.adventure.text.*;
import team.floracore.common.plugin.bootstrap.*;
import team.floracore.common.sender.*;

import java.util.*;

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

    Args0 TRANSLATIONS_SEARCHING = () -> prefixed(translatable()
            .key("floracore.command.translations.searching")
            .color(GRAY)
    );

    Args0 TRANSLATIONS_SEARCHING_ERROR = () -> prefixed(text()
            .color(RED)
            .append(translatable("floracore.command.translations.searching-error"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("floracore.command.misc.check-console-for-errors"))
            .append(FULL_STOP)
    );


    Args1<Collection<String>> INSTALLED_TRANSLATIONS = locales -> prefixed(translatable()
            .key("floracore.command.translations.installed-translations")
            .color(GREEN)
            .append(text(':'))
            .append(space())
            .append(formatStringList(locales))
    );


    Args4<String, String, Integer, List<String>> AVAILABLE_TRANSLATIONS_ENTRY = (tag, name, percentComplete, contributors) -> prefixed(text()
            // - {} ({}) - {}% translated - by {}
            .color(GRAY)
            .append(text('-'))
            .append(space())
            .append(text(tag, AQUA))
            .append(space())
            .append(OPEN_BRACKET)
            .append(text(name, WHITE))
            .append(CLOSE_BRACKET)
            .append(text(" - "))
            .append(translatable("floracore.command.translations.percent-translated", text(percentComplete, GREEN)))
            .apply(builder -> {
                if (!contributors.isEmpty()) {
                    builder.append(text(" - "));
                    builder.append(translatable("floracore.command.translations.translations-by"));
                    builder.append(space());
                    builder.append(formatStringList(contributors));
                }
            })
    );

    Args0 TRANSLATIONS_DOWNLOAD_PROMPT = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder()
                .separator(newline())
                .build();
        return join(joinConfig,
            prefixed(translatable()
                    .key("floracore.command.translations.download-prompt")
                    .color(AQUA)
                    .args(text("/fc translations install", GREEN))
                    .append(FULL_STOP)),
            prefixed(translatable()
                    .key("floracore.command.translations.download-override-warning")
                    .color(GRAY)
                    .append(FULL_STOP)));
    };

    Args0 AVAILABLE_TRANSLATIONS_HEADER = () -> prefixed(translatable()
            .key("floracore.command.translations.available-translations")
            .color(GREEN)
            .append(text(':'))
    );

    Args1<String> TRANSLATIONS_INSTALLING_SPECIFIC = name -> prefixed(translatable()
            .key("floracore.command.translations.installing-specific")
            .color(GREEN)
            .args(text(name))
    );

    Args1<String> TRANSLATIONS_DOWNLOAD_ERROR = name -> prefixed(text()
            .color(RED)
            .append(translatable("floracore.command.translations.download-error", text(name, DARK_RED)))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("floracore.command.misc.check-console-for-errors"))
            .append(FULL_STOP)
    );

    Args0 TRANSLATIONS_INSTALLING = () -> prefixed(translatable()
            .key("floracore.command.translations.installing")
            .color(AQUA)
    );

    Args0 TRANSLATIONS_INSTALL_COMPLETE = () -> prefixed(translatable()
            .key("floracore.command.translations.install-complete")
            .color(AQUA)
            .append(FULL_STOP)
    );

    Args0 RELOAD_CONFIG_SUCCESS = () -> prefixed(translatable()
            .key("floracore.command.reload-config.success")
            .color(GREEN)
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable("floracore.command.reload-config.restart-note"))
                    .append(CLOSE_BRACKET)
            )
    );

    Args0 NO_PERMISSION_FOR_SUBCOMMANDS = () -> prefixed(translatable()
            .key("floracore.commandsystem.no-permission-subcommands")
            .color(DARK_AQUA)
            .append(FULL_STOP)
    );

    Args0 COMMAND_NO_PERMISSION = () -> prefixed(translatable()
            .key("floracore.commandsystem.no-permission")
            .color(RED)
    );

    Args1<String> COMMAND_INVALID_COMMAND_SYNTAX = correctSyntax -> prefixed(text()
            .color(RED)
            .append(translatable("floracore.commandsystem.invalid-command-syntax", text(correctSyntax,DARK_AQUA)))
            .append(FULL_STOP)
    );

    Args0 COMMAND_FLY_ENABLE_SELF = () -> prefixed(translatable()
            .key("floracore.command.fly.self")
            .color(AQUA)
            .args(translatable("floracore.command.misc.on").color(GREEN))
    );

    Args0 COMMAND_FLY_DISABLE_SELF = () -> prefixed(translatable()
            .key("floracore.command.fly.self")
            .color(AQUA)
            .args(translatable("floracore.command.misc.on").color(RED))
    );

    Args1<String> COMMAND_FLY_ENABLE_OTHER = target -> prefixed(translatable()
            .key("floracore.command.fly.other")
            .color(AQUA)
            .args(text(target).color(GREEN), translatable("floracore.command.misc.on").color(GREEN))
    );

    Args1<String> COMMAND_FLY_DISABLE_OTHER = target -> prefixed(translatable()
            .key("floracore.command.fly.other")
            .color(AQUA)
            .args(text(target).color(GREEN), translatable("floracore.command.misc.off").color(RED))
    );

    Args1<String> COMMAND_FLY_ENABLE_FROM = from -> prefixed(translatable()
            .key("floracore.command.fly.from")
            .color(AQUA)
            .args(text(from).color(GREEN), translatable("floracore.command.misc.on").color(GREEN))
    );

    Args1<String> COMMAND_FLY_DISABLE_FROM = from -> prefixed(translatable()
            .key("floracore.command.fly.from")
            .color(AQUA)
            .args(text(from).color(GREEN), translatable("floracore.command.misc.on").color(GREEN))
    );

    static TextComponent prefixed(ComponentLike component) {
        return text()
                .append(PREFIX_COMPONENT)
                .append(space())
                .append(component)
                .build();
    }

    static Component formatStringList(Collection<String> strings) {
        Iterator<String> it = strings.iterator();
        if (!it.hasNext()) {
            return translatable("floracore.command.misc.none", AQUA);
        }

        TextComponent.Builder builder = text().color(DARK_AQUA).content(it.next());

        while (it.hasNext()) {
            builder.append(text(", ", GRAY));
            builder.append(text(it.next()));
        }

        return builder.build();
    }

    static Component formatBoolean(boolean bool) {
        return bool ? text("true", GREEN) : text("false", RED);
    }

    interface Args0 {
        Component build();

        default void send(Sender sender) {
            sender.sendMessage(build());
        }
    }

    interface Args1<A0> {
        Component build(A0 arg0);

        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }
    }

    interface Args2<A0, A1> {
        Component build(A0 arg0, A1 arg1);

        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }
    }

    interface Args3<A0, A1, A2> {
        Component build(A0 arg0, A1 arg1, A2 arg2);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2) {
            sender.sendMessage(build(arg0, arg1, arg2));
        }
    }

    interface Args4<A0, A1, A2, A3> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3));
        }
    }

    interface Args5<A0, A1, A2, A3, A4> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4));
        }
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5));
        }
    }
}
