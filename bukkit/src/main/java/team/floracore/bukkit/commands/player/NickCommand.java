package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import me.neznamy.tab.api.*;
import me.neznamy.tab.api.team.*;
import net.kyori.adventure.audience.*;
import net.kyori.adventure.inventory.*;
import net.kyori.adventure.text.*;
import net.luckperms.api.*;
import net.luckperms.api.model.user.*;
import net.luckperms.api.node.types.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.signgui.*;
import team.floracore.bukkit.util.wrappedmojang.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.common.config.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;
import java.util.concurrent.*;

import static net.kyori.adventure.text.Component.*;

/**
 * Nick命令
 */
@CommandPermission("floracore.command.nick")
@CommandDescription("修改玩家的昵称")
public class NickCommand extends FloraCoreBukkitCommand implements Listener {
    private final LuckPerms luckPerms;
    private final ConcurrentHashMap<UUID, NmsBlockPosition> signBP = new ConcurrentHashMap<>();

    public NickCommand(FCBukkitPlugin plugin) {
        super(plugin);
        // this.skinsRestorerAPI = SkinsRestorerAPI.getApi();
        this.luckPerms = LuckPermsProvider.get();
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("nick")
    @CommandDescription("更改你的昵称")
    public void nick(final @NotNull Player p) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        bookNick(p, 0, null, null, null, null);
    }

    @CommandMethod("nick <name>")
    @CommandDescription("将你的昵称修改为一个指定的昵称")
    @CommandPermission("floracore.command.nick.custom")
    public void nickSpecifiedName(final @NotNull Player p, final @Argument("name") String name) {
        UUID uuid = p.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        Audience target = getPlugin().getSenderFactory().getAudiences().player(p);
        DATA statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        Map<String, String> ranks_prefix = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PREFIX);
        if (statusData != null && Boolean.parseBoolean(statusData.getValue())) {
            PlayerCommandMessage.COMMAND_MISC_NICK_ALREADY_NICKED.send(sender);
            return;
        }
        if (checkNicknameLegitimacy(p, name)) {
            performNick(p, "rank0", "random", name, true);
            target.openBook(getFinishPage(ranks_prefix.get("rank0"), name, uuid));
        }
    }

    @CommandMethod("unnick")
    @CommandDescription("取消你的昵称")
    public void unNick(final @NotNull Player p) {
        UUID uuid = p.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        DATA statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        if (statusData != null && Boolean.parseBoolean(statusData.getValue())) {
            performUnNick(p);
            PlayerCommandMessage.COMMAND_UNNICK_SUCCESS.send(sender);
        } else {
            PlayerCommandMessage.COMMAND_UNNICK_NOT_IN.send(sender);
        }
    }

    @CommandMethod("nick reset")
    @CommandDescription("取消你的昵称")
    public void nickReset(final @NotNull Player p) {
        unNick(p);
    }

    @CommandMethod("book-nick <page> [rank] [skin] [name] [nickname]")
    @CommandDescription("根据页面召唤Nick书本")
    public void bookNick(final @NotNull Player p, final @Argument("page") Integer page, final @Argument("rank") String rank, final @Argument("skin") SkinType skin, final @Argument("name") String name, @Argument("nickname") String nickname) {
        UUID uuid = p.getUniqueId();
        Audience target = getPlugin().getSenderFactory().getAudiences().player(p);
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK);
        Map<String, String> ranks_permission = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PERMISSION);
        Map<String, String> ranks_prefix = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PREFIX);
        boolean custom = p.hasPermission("floracore.command.nick.custom");
        DATA statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        if (statusData != null && Boolean.parseBoolean(statusData.getValue())) {
            PlayerCommandMessage.COMMAND_MISC_NICK_ALREADY_NICKED.send(sender);
            return;
        }
        if (rank != null) {
            if (ranks.containsKey(rank)) {
                String permission = ranks_permission.get(rank);
                if (!p.hasPermission(permission)) {
                    PlayerCommandMessage.COMMAND_MISC_NICK_RANK_NO_PERMISSION.send(sender, rank);
                    return;
                }
            } else {
                PlayerCommandMessage.COMMAND_MISC_NICK_RANK_UNKNOWN.send(sender, rank);
                return;
            }
        }

        try {
            switch (page) {
                case 0:
                    // start page
                    target.openBook(getStartPage(uuid));
                    break;
                case 1:
                    // rank page
                    target.openBook(getRankPage(p));
                    break;
                case 2:
                    // skin page
                    BookMessage.COMMAND_NICK_SETUP_RANK.send(sender, ranks.get(rank));
                    target.openBook(getSkinPage(p, rank));
                    break;
                case 3:
                    // name page
                    Component sc = null;
                    if (skin == SkinType.STEVE_ALEX) {
                        sc = BookMessage.COMMAND_MISC_NICK_SKIN_STEVE_ALEX.build();
                    } else if (skin == SkinType.RANDOM) {
                        sc = BookMessage.COMMAND_MISC_NICK_SKIN_RANDOM.build();
                    } else if (skin == SkinType.REUSE) {
                        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.skin");
                        if (data != null) {
                            sc = BookMessage.COMMAND_MISC_NICK_SKIN_REUSE.build(data.getValue());
                        }
                    } else {
                        sc = BookMessage.COMMAND_MISC_NICK_SKIN_NORMAL.build();
                    }
                    PlayerCommandMessage.COMMAND_NICK_SETUP_SKIN.send(sender, sc);
                    target.openBook(getNamePage(p, rank, skin));
                    break;
                case 4:
                    if (name.equalsIgnoreCase("custom") && custom) {
                        bookNick(p, 6, rank, skin, name, nickname);
                    } else {
                        if (name.equalsIgnoreCase("random") && custom) {
                            target.openBook(getRandomPage(rank, skin, uuid));
                            return;
                        }
                        if (name.equalsIgnoreCase("reuse")) {
                            DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.name");
                            nickname = (data != null) ? data.getValue() : getPlugin().getNamesRepository().getRandomNameProperty().getName();
                        } else if (name.equalsIgnoreCase("random") && !custom) {
                            nickname = getPlugin().getNamesRepository().getRandomNameProperty().getName();
                        }
                        performNick(p, rank, skin.name(), nickname, true);
                        target.openBook(getFinishPage(ranks_prefix.get(rank), nickname, uuid));
                    }
                    break;
                case 5:
                    // random nick
                    if (custom) {
                        if (name.equalsIgnoreCase("random")) {
                            if (nickname == null) {
                                MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
                            } else {
                                performNick(p, rank, skin.name(), nickname, true);
                                target.openBook(getFinishPage(ranks_prefix.get(rank), nickname, uuid));
                                BookMessage.COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_1_MESSAGE.send(sender);
                            }
                        } else {
                            MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
                        }
                    } else {
                        MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                    }
                    break;
                case 6:
                    // custom page
                    String line1 = "";
                    String l1j = NmsIChatBaseComponent.NmsChatSerializer.getJson(line1);
                    NmsIChatBaseComponent il1 = NmsIChatBaseComponent.NmsChatSerializer.jsonToComponent(l1j);
                    Component l2c = TranslationManager.render(SignMessage.COMMAND_MISC_NICK_SIGN_LINE_2.build(), uuid);
                    Component l3c = TranslationManager.render(SignMessage.COMMAND_MISC_NICK_SIGN_LINE_3.build(), uuid);
                    Component l4c = TranslationManager.render(SignMessage.COMMAND_MISC_NICK_SIGN_LINE_4.build(), uuid);
                    String line2 = TranslationManager.SERIALIZER.serialize(l2c);
                    /*String l2j = NmsIChatBaseComponent.NmsChatSerializer.getJson(line2);
                    NmsIChatBaseComponent il2 = NmsIChatBaseComponent.NmsChatSerializer.jsonToComponent(l2j);*/
                    String line3 = TranslationManager.SERIALIZER.serialize(l3c);
                    /*String l3j = NmsIChatBaseComponent.NmsChatSerializer.getJson(line3);
                    NmsIChatBaseComponent il3 = NmsIChatBaseComponent.NmsChatSerializer.jsonToComponent(l3j);*/
                    String line4 = TranslationManager.SERIALIZER.serialize(l4c);
                    /*String l4j = NmsIChatBaseComponent.NmsChatSerializer.getJson(line4);
                    NmsIChatBaseComponent il4 = NmsIChatBaseComponent.NmsChatSerializer.jsonToComponent(l4j);
                    World world = p.getWorld();
                    Location location = p.getLocation().add(0, 10,0);
                    NmsBlockPosition nbp = NmsBlockPosition.newInstance(location);
                    NmsPacketPlayOutOpenSignEditor openSignEditorPacket = NmsPacketPlayOutOpenSignEditor.newInstance(nbp);
                    NmsWorld nw = WrappedObject.wrap(ObcWorld.class, world).getHandle();
                    NmsIChatBaseComponentArray nicbc = NmsIChatBaseComponentArray.newInstance(4);
                    nicbc.set(0, il1);
                    nicbc.set(1, il2);
                    nicbc.set(2, il3);
                    nicbc.set(3, il4);
                    NmsPacketPlayOutUpdateSign updateSignPacket = NmsPacketPlayOutUpdateSign.newInstance(nw, nbp, nicbc);
                    ItemStack itemStack = ItemStackBuilder.sign().get();
                    Material material = itemStack.getType();
                    location.getBlock().setType(material);
                    ProtocolUtil.sendPacket(p, updateSignPacket);
                    ProtocolUtil.sendPacket(p, openSignEditorPacket);
                    ProtocolUtil.instance.register(new ProtocolUtil.ReceiveListener(EventPriority.NORMAL, NmsPacketPlayInUpdateSign.class, (player, packet, cancelled) -> {
                        NmsPacketPlayInUpdateSign sign = (NmsPacketPlayInUpdateSign) packet;
                        NmsIChatBaseComponentArray lines = sign.getIChatBaseComponents();
                        System.out.println(NmsIChatBaseComponent.NmsChatSerializer.toJson(lines.get(0)));
                    }));*/
                    SignGUIAPI signGUIAPI = new SignGUIAPI(event -> {
                        String i = event.getLines().get(0);
                        if (checkNicknameLegitimacy(p, i)) {
                            performNick(p, rank, skin.name(), i, true);
                            target.openBook(getFinishPage(ranks_prefix.get(rank), i, uuid));
                        }

                    }, Arrays.asList(line1, line2, line3, line4), uuid, getPlugin().getLoader());
                    signGUIAPI.open();
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
        }
    }

    public boolean checkNicknameLegitimacy(Player p, String name) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        int nameLengthMin = 3;
        int nameLengthMax = 16;
        if (name.contains(" ")) {
            // 名字包含空格
            PlayerCommandMessage.COMMAND_MISC_NICK_NAME_ILLEGAL_SPACE.send(sender);
            return false;
        } else if (name.length() < nameLengthMin || name.length() > nameLengthMax) {
            // 名字长度不符合要求
            PlayerCommandMessage.COMMAND_MISC_NICK_NAME_ILLEGAL_LENGTH.send(sender);
            return false;
        } else if (!name.matches("[a-zA-Z0-9_]+")) {
            // 名字不符合Minecraft命名规则
            PlayerCommandMessage.COMMAND_MISC_NICK_NAME_ILLEGAL_CHARACTER.send(sender);
            return false;
        } else {
            // 执行相应操作
            return true;
        }
    }

    private void performUnNick(Player p) {
        UUID uuid = p.getUniqueId();
        DATA statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        // 重置昵称
        String name = getPlayerRecordName(uuid);
        resetName(p, name);
        // 重置rank
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            prefix = prefix == null ? "" : prefix;
            PrefixNode node = PrefixNode.builder(prefix, 77665).build();
            user.data().remove(node);
            luckPerms.getUserManager().saveUser(user);
        }
        // TODO 重置皮肤
        // 清除数据库Nick状态
        getAsyncExecutor().execute(() -> {
            DATA rankData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.rank");
            if (statusData != null) {
                getStorageImplementation().deleteDataID(statusData.getId());
            }
            if (rankData != null) {
                getStorageImplementation().deleteDataID(rankData.getId());
            }
        });
    }

    private void performNick(Player p, String rank, String skin, String name, boolean typeNick) {
        UUID uuid = p.getUniqueId();
        Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PREFIX);
        // 修改玩家信息
        resetName(p, name);
        // TODO 设置皮肤
        // 设置Rank
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            PrefixNode node = PrefixNode.builder(ranks.get(rank), 77665).build();
            user.data().add(node);
            luckPerms.getUserManager().saveUser(user);
        }

        // 设置数据库Nick状态
        getAsyncExecutor().execute(() -> {
            getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.name", name, 0);
            getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.rank", rank, 0);
            getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.status", String.valueOf(true), 0);
        });
    }

    private void resetName(Player player, String name) {
        UUID uuid = player.getUniqueId();
        NmsEntityPlayer nep = WrappedObject.wrap(ObcEntity.class, player).getHandle().cast(NmsEntityPlayer.class);
        NmsEnumPlayerInfoAction removePlayer = WrappedObject.getStatic(NmsEnumPlayerInfoAction.class).REMOVE_PLAYER();
        NmsPacketPlayOutPlayerInfo removePacket = NmsPacketPlayOutPlayerInfo.newInstance(removePlayer, Collections.singletonList(nep.getRaw()));
        ProtocolUtil.sendPacketToAllPlayers(removePacket);
        WrappedGameProfile wgp = nep.getGameProfile();
        wgp.setName(name);
        player.setDisplayName(name);
        player.setPlayerListName(null);
        NmsEnumPlayerInfoAction addPlayer = WrappedObject.getStatic(NmsEnumPlayerInfoAction.class).ADD_PLAYER();
        NmsPacketPlayOutPlayerInfo addPacket = NmsPacketPlayOutPlayerInfo.newInstance(addPlayer, Collections.singletonList(nep.getRaw()));
        ProtocolUtil.sendPacketToAllPlayers(addPacket);
        getAsyncExecutor().execute(() -> {
            if (getPlugin().getLoader().getServer().getPluginManager().getPlugin("TAB") != null) {
                TabPlayer tp = TabAPI.getInstance().getPlayer(uuid);
                TablistFormatManager tfm = TabAPI.getInstance().getTablistFormatManager();
                tfm.setName(tp, name);
                TeamManager tm = TabAPI.getInstance().getTeamManager();
                if (tm instanceof UnlimitedNametagManager) {
                    UnlimitedNametagManager unm = (UnlimitedNametagManager) tm;
                    unm.setName(tp, name);
                }
            }
        });
    }

    private Book getStartPage(UUID uuid) {
        Component bookTitle = text("FloraCore Nick StartPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component line1 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_1.build(), uuid);
        Component line2 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_2.build(), uuid);
        Component line3 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_START_PAGE_LINE_3.build(), uuid);
        Component accept = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_START_PAGE_ACCEPT_TEXT.build(), uuid);
        Component component = join(joinConfig, line1, space(),
                // line 2
                line2,
                // line 3
                line3, space(),
                // accept
                accept).asComponent();
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getRankPage(Player player) {
        UUID uuid = player.getUniqueId();
        Component bookTitle = text("FloraCore Nick RankPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component line1 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_1.build(), uuid);
        Component line2 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANK_PAGE_LINE_2.build(), uuid);
        Component component = join(joinConfig, line1,
                // line 2
                line2, space()).asComponent();
        Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK);
        for (Map.Entry<String, String> entry : ranks.entrySet()) {
            String rankName = entry.getKey();
            String rankPermission = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PERMISSION).get(rankName);
            if (player.hasPermission(rankPermission)) {
                Component rank = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANK_PAGE_RANK.build(rankName, entry.getValue()));
                component = join(joinConfig, component, rank);
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
        Component line1 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_LINE_1.build(), uuid);
        Component normal = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_NORMAL.build(rank), uuid);
        Component classic = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_STEVE_ALEX.build(rank), uuid);
        Component random = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_RANDOM.build(rank), uuid);
        Component component = join(joinConfig, line1, space(),
                // normal skin
                normal,
                // steve / alex skin
                classic,
                // random skin
                random).asComponent();
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.skin");
        if (data != null) {
            // reuse skin
            Component reuse = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_REUSE.build(rank, data.getValue()), uuid);
            component = join(joinConfig, component, reuse);
        }
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getNamePage(Player player, String rank, SkinType skin) {
        UUID uuid = player.getUniqueId();
        Component bookTitle = text("FloraCore Nick NamePage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component line1 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_NAME_PAGE_LINE_1.build(), uuid);
        Component random = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_NAME_PAGE_RANDOM.build(rank, skin), uuid);
        Component component = join(joinConfig, line1, space(),
                // random name
                random).asComponent();
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.name");
        if (data != null) {
            // reuse name
            Component reuse = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_NAME_PAGE_REUSE.build(rank, skin, data.getValue()), uuid);
            component = join(joinConfig, component, reuse);
        }
        if (player.hasPermission("floracore.command.nick.custom")) {
            Component custom = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_NAME_PAGE_CUSTOM.build(rank, skin), uuid);
            component = join(joinConfig, component, custom);
        }
        Component reset = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RESET.build(), uuid);
        component = join(joinConfig, component, space(), reset);
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getRandomPage(String rank, SkinType skin, UUID uuid) {
        Component bookTitle = text("FloraCore Nick RandomPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        String nickname = getPlugin().getNamesRepository().getRandomNameProperty().getName();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component line1 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_LINE_1.build(), uuid);
        Component name = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_NAME.build(nickname), uuid);
        Component use = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_USE_NAME.build(rank, skin, nickname), uuid);
        Component again = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_TRY_AGAIN.build(rank, skin), uuid);
        Component custom = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_CUSTOM.build(rank, skin), uuid);
        Component component = join(joinConfig, line1,
                // name
                name, space(),
                // use
                use,
                // try
                again, space(),
                // custom
                custom).asComponent();
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    private Book getFinishPage(String rank, String name, UUID uuid) {
        Component bookTitle = text("FloraCore Nick FinishPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component line1 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_1.build(), uuid);
        Component line2 = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_FINISH_PAGE_LINE_2.build(rank, name), uuid);
        Component reset = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_RESET.build(), uuid);
        Component component = join(joinConfig, line1, space(),
                // line 2
                line2, space(),
                // reset
                reset).asComponent();
        bookPages.add(component);
        return Book.book(bookTitle, bookAuthor, bookPages);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID u = p.getUniqueId();
        DATA data = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.status");
        if (data != null) {
            String value = data.getValue();
            boolean nick = Boolean.parseBoolean(value);
            if (nick && p.hasPermission("floracore.command.nick")) {
                Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK);
                Map<String, String> ranks_permission = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PERMISSION);
                DATA rankData = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.rank");
                DATA skinData = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.skin");
                DATA nameData = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.name");
                if (rankData != null && skinData != null && nameData != null) {
                    String rank = rankData.getValue();
                    String skin = skinData.getValue();
                    String name = nameData.getValue();
                    if (rank != null && skin != null && name != null) {
                        if (ranks.containsKey(rank)) {
                            String permission = ranks_permission.get(rank);
                            if (p.hasPermission(permission)) {
                                performNick(p, rank, skin, name, false);
                            } else {
                                performUnNick(p);
                            }
                        } else {
                            performUnNick(p);
                        }
                    } else {
                        performUnNick(p);
                    }
                }
            }
        }
    }

    public enum SkinType {
        NORMAL,
        STEVE_ALEX,
        RANDOM,
        REUSE
    }
}