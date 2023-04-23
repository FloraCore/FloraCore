package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import net.kyori.adventure.audience.*;
import net.kyori.adventure.inventory.*;
import net.kyori.adventure.text.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.api.data.*;
import team.floracore.common.command.*;
import team.floracore.common.config.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;
import team.floracore.common.util.craftbukkit.signgui.*;

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
        bookNick(p, 0, null, null, null, null);
    }

    @CommandMethod("nick <name>")
    @CommandDescription("将你的昵称修改为一个指定的昵称")
    public void nickSpecifiedName(final @NotNull Player p, final @Argument("name") String name) {

    }

    @CommandMethod("book-nick <page> [rank] [skin] [name] [nickname]")
    @CommandDescription("根据页面召唤Nick书本")
    public void bookNick(final @NotNull Player p, final @Argument("page") Integer page, final @Argument("rank") String rank, final @Argument("skin") String skin, final @Argument("name") String name, @Argument("nickname") String nickname) {
        UUID uuid = p.getUniqueId();
        Audience target = getPlugin().getBukkitAudiences().player(p);
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK);
        Map<String, String> ranks_prefix = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PREFIX);
        Map<String, String> sign = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_SIGN);
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        boolean custom = p.hasPermission("floracore.command.nick.custom");
        try {

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
                    Message.COMMAND_NICK_SETUP_RANK.send(sender, ranks.get(rank));
                    target.openBook(getSkinPage(p, rank));
                    break;
                case 3:
                    // name page
                    Message.COMMAND_NICK_SETUP_SKIN.send(sender);
                    target.openBook(getNamePage(p, rank, skin));
                    break;
                case 4:
                    if (name.equalsIgnoreCase("custom") && custom) {
                        bookNick(p, 6, rank, skin, name, nickname);
                    } else {
                        // finish page
                        if (name.equalsIgnoreCase("reuse")) {
                            Data data = storageImplementation.getSpecifiedData(uuid, DataType.FUNCTION, "nick.name.reuse");
                            nickname = (data != null) ? data.getValue() : FakerUtil.getRandomLegalName();
                        } else if (name.equalsIgnoreCase("random")) {
                            if (custom) {
                                bookNick(p, 5, rank, skin, name, nickname);
                            } else {
                                nickname = FakerUtil.getRandomLegalName();
                            }
                        }
                        target.openBook(getFinishPage(ranks_prefix.get(rank), nickname));
                    }
                    break;
                case 5:
                    // random page
                    break;
                case 6:
                    // custom page
                    String line1 = sign.get("line" + 1);
                    String line2 = sign.get("line" + 2);
                    String line3 = sign.get("line" + 3);
                    String line4 = sign.get("line" + 4);
                    SignGUIAPI signGUIAPI = new SignGUIAPI(event -> {
                        String i = event.getLines().get(0);
                        int nameLengthMin = Math.min(3, 16), nameLengthMax = Math.max(16, 1);
                        if (!(i.isEmpty()) && (i.length() <= nameLengthMax) && (i.length() >= nameLengthMin)) {
                            System.out.println(i);
                            // utils.performRankedNick(player, rankName, skinType, name);
                        }
                    }, Arrays.asList(line1, line2, line3, line4), uuid, getPlugin().getBootstrap().getPlugin());
                    signGUIAPI.open();
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Message.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
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
        UUID uuid = player.getUniqueId();
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
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        Data data = storageImplementation.getSpecifiedData(uuid, DataType.FUNCTION, "nick.skin.reuse");
        if (data != null) {
            // reuse skin
            component = join(joinConfig, component, Message.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_REUSE.build(rank, data.getValue()));
        }
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getNamePage(Player player, String rank, String skin) {
        UUID uuid = player.getUniqueId();
        Component bookTitle = text("FloraCore Nick NamePage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component component = join(joinConfig, Message.COMMAND_MISC_NICK_BOOK_NAME_PAGE_LINE_1.build(), space(),
                // random name
                Message.COMMAND_MISC_NICK_BOOK_NAME_PAGE_RANDOM.build(rank, skin)).asComponent();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        Data data = storageImplementation.getSpecifiedData(uuid, DataType.FUNCTION, "nick.name.reuse");
        if (data != null) {
            // reuse name
            component = join(joinConfig, component, Message.COMMAND_MISC_NICK_BOOK_NAME_PAGE_REUSE.build(rank, skin, data.getValue()));
        }
        if (player.hasPermission("floracore.command.nick.custom")) {
            component = join(joinConfig, component, Message.COMMAND_MISC_NICK_BOOK_NAME_PAGE_CUSTOM.build(rank, skin));
        }
        component = join(joinConfig, component, space(), Message.COMMAND_MISC_NICK_BOOK_RESET.build());
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getFinishPage(String rank, String name) {
        Component bookTitle = text("FloraCore Nick FinishPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component component = join(joinConfig, Message.COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_1.build(), space(),
                // line 2
                Message.COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_2.build(rank, name), space(),
                // line 3
                Message.COMMAND_MISC_NICK_BOOK_RESET.build()).asComponent();
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

}
