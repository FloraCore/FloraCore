package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import net.kyori.adventure.audience.*;
import net.kyori.adventure.inventory.*;
import net.kyori.adventure.text.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.config.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;

import java.util.*;

import static net.kyori.adventure.text.Component.*;

@CommandPermission("floracore.command.nick")
public class NickCommand extends AbstractFloraCoreCommand {
    public NickCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("nick")
    @CommandDescription("更改你的昵称")
    public void nick(final @NotNull Player p) {
        bookNick(p, 0, null, null, null);
    }

    @CommandMethod("nick <name>")
    @CommandDescription("将你的昵称修改为一个指定的昵称")
    public void nickSpecifiedName(final @NotNull Player p, final @Argument("name") String name) {

    }

    @CommandMethod("book-nick <page> [rank] [skin] [name]")
    @CommandDescription("根据页面召唤Nick书本")
    public void bookNick(final @NotNull Player p, final @Argument("page") Integer page, final @Argument("rank") String rank, final @Argument("skin") String skin, final @Argument("name") String name) {
        Audience target = getPlugin().getBukkitAudiences().player(p);
        switch (page) {
            case 0:
                // start page
                target.openBook(getStartPage());
                break;
            case 1:
                // rank page
                target.openBook(getRankPage(p));
                break;
            case 2:
                // skin page
                target.openBook(getSkinPage(p, rank));
                break;
            case 3:
                // name page
                // rank skin
                break;
        }
    }

    private Book getStartPage() {
        Component bookTitle = text("FloraCore Nick StartPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component component = join(joinConfig, Message.COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_1.build(), space(),
                // line 2
                Message.COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_2.build(),
                // line 3
                Message.COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_3.build(), space(),
                // accept
                Message.COMMAND_MISC_NICK_BOOK_START_PAGE_ACCEPT_TEXT.build()).asComponent();
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getRankPage(Player player) {
        Component bookTitle = text("FloraCore Nick RankPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component component = join(joinConfig, Message.COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_1.build(),
                // line 2
                Message.COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_2.build(), space()).asComponent();
        Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK);
        for (Map.Entry<String, String> entry : ranks.entrySet()) {
            String rankName = entry.getKey();
            String rankPermission = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PERMISSION).get(rankName);
            if (player.hasPermission(rankPermission)) {
                component = join(joinConfig, component, Message.COMMAND_MISC_NICK_BOOK_RANK_PAGE_RANK.build(rankName, entry.getValue()));
            }
        }
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getSkinPage(Player player, String rank) {
        Component bookTitle = text("FloraCore Nick SkinPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component component = join(joinConfig, Message.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_LINE_1.build(), space(),
                // normal skin
                Message.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_NORMAL.build(rank),
                // steve / alex skin
                Message.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_STEVE_ALEX.build(rank),
                // random skin
                Message.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_RANDOM.build(rank)).asComponent();
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

}
