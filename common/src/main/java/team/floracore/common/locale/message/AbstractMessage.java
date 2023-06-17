package team.floracore.common.locale.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.floracore.api.FloraCore;
import org.floracore.api.FloraCoreProvider;
import team.floracore.common.sender.Sender;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public abstract interface AbstractMessage {
    TextComponent OPEN_BRACKET = Component.text('(');
    TextComponent CLOSE_BRACKET = Component.text(')');
    TextComponent FULL_STOP = Component.text('.');
    TextComponent ARROW = Component.text('➤');
    TextComponent ARROW_LIGHT = Component.text(">");
    TextComponent CIRCLE = Component.text('●');
    TextComponent BOX = Component.text('■');
    TextComponent COLON = Component.text(':');
    TextComponent HORIZONTAL_LINE = Component.text('-');
    TextComponent HORIZONTAL_LINES = text("-----------------------------------------------------");

    Component PREFIX_COMPONENT = text()
            // [FC]
            .color(GRAY)
            .append(text('['))
            .append(text().decoration(BOLD, true).append(text('F', AQUA)).append(text('C', YELLOW)))
            .append(text(']'))
            .build();

    Args1<UUID> RENDER_PLAYER_NAME = (uuid) -> {
        FloraCore floraCore = FloraCoreProvider.get();
        String sn = floraCore.getPlayerAPI().getPlayerRecordName(uuid);
        String prefix = floraCore.getPlayerAPI().getPrefix(uuid);
        prefix = prefix == null ? "" : prefix + " ";
        String suffix = floraCore.getPlayerAPI().getSuffix(uuid);
        suffix = suffix == null ? "" : suffix;
        return AbstractMessage.formatColoredValue(prefix)
                .append(text(sn))
                .append(AbstractMessage.formatColoredValue(suffix));
    };

    Args3<Integer, Integer, Integer> PROGRESS = (len, now, total) -> {
        Component c1 = text().color(AQUA).build();
        Component c2 = text().color(GRAY).build();

        if (now >= total) {
            for (int i = 0; i < len; i++) {
                c1 = c1.append(BOX);
            }
        } else {
            int completedLen = now / total * len;
            int remainingLen = len - completedLen;
            for (int a = 0; a < completedLen; a++) {
                c1 = c1.append(BOX);
            }
            for (int b = 0; b < remainingLen; b++) {
                c2 = c2.append(BOX);
            }
        }

        return c1.append(c2);
    };

    static TextComponent prefixed(ComponentLike component) {
        return text().append(PREFIX_COMPONENT).append(space()).append(component).build();
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

    static Component formatColoredValue(String value) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(value).toBuilder().build();
    }

    interface Args0 {
        default void send(Sender sender) {
            sender.sendMessage(build());
        }

        Component build();
    }

    interface Args1<A0> {
        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }

        Component build(A0 arg0);
    }

    interface Args2<A0, A1> {
        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }

        Component build(A0 arg0, A1 arg1);
    }

    interface Args3<A0, A1, A2> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2) {
            sender.sendMessage(build(arg0, arg1, arg2));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2);
    }

    interface Args4<A0, A1, A2, A3> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);
    }

    interface Args5<A0, A1, A2, A3, A4> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
    }

    interface Args7<A0, A1, A2, A3, A4, A5, A6> {
        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 args6) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5, args6));
        }

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 args6);
    }
}
