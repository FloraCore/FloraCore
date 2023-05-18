package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import net.kyori.adventure.audience.*;
import net.kyori.adventure.inventory.*;
import net.kyori.adventure.text.*;
import net.luckperms.api.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.config.*;
import team.floracore.common.locale.data.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.craftbukkit.*;
import team.floracore.common.util.craftbukkit.signgui.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import static net.kyori.adventure.text.Component.*;
import static team.floracore.common.util.ReflectionWrapper.*;

/**
 * Nick命令
 */
@CommandPermission("floracore.command.nick")
@CommandDescription("修改玩家的昵称")
public class NickCommand extends AbstractFloraCoreCommand implements Listener {
    // private final SkinsRestorerAPI skinsRestorerAPI;
    private final Set<UUID> nickedPlayers = new HashSet<>();
    private final LuckPerms luckPerms;

    public NickCommand(FloraCorePlugin plugin) {
        super(plugin);
        // this.skinsRestorerAPI = SkinsRestorerAPI.getApi();
        this.luckPerms = LuckPermsProvider.get();
        plugin.getListenerManager().registerListener(this);
        plugin.getBootstrap().getScheduler().asyncRepeating(() -> {
            for (UUID uuid : nickedPlayers) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    Audience target = getPlugin().getBukkitAudiences().player(p);
                    target.sendActionBar(Message.COMMAND_MISC_NICK_ACTION_BAR.build());
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    @CommandMethod("nick")
    @CommandDescription("更改你的昵称")
    public void nick(final @NotNull Player p) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (whetherServerEnableAutoSync2()) {
            MiscMessage.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
            return;
        }
        bookNick(p, 0, null, null, null, null);
    }

    @CommandMethod("nick <name>")
    @CommandDescription("将你的昵称修改为一个指定的昵称")
    @CommandPermission("floracore.command.nick.custom")
    public void nickSpecifiedName(final @NotNull Player p, final @Argument("name") String name) {
        UUID uuid = p.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (whetherServerEnableAutoSync2()) {
            MiscMessage.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
            return;
        }
        Audience target = getPlugin().getBukkitAudiences().player(p);
        performNick(p, "rank0", "random", name, true);
        target.openBook(getFinishPage("rank0", name, uuid));
    }

    @CommandMethod("unnick")
    @CommandDescription("取消你的昵称")
    public void unNick(final @NotNull Player p) {
        UUID uuid = p.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (whetherServerEnableAutoSync2()) {
            MiscMessage.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
            return;
        }
        DATA statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        if (statusData != null && Boolean.parseBoolean(statusData.getValue())) {
            performUnNick(p);
            Message.COMMAND_UNNICK_SUCCESS.send(sender);
        } else {
            Message.COMMAND_UNNICK_NOT_IN.send(sender);
        }
    }

    @CommandMethod("book-nick <page> [rank] [skin] [name] [nickname]")
    @CommandDescription("根据页面召唤Nick书本")
    public void bookNick(final @NotNull Player p, final @Argument("page") Integer page, final @Argument("rank") String rank, final @Argument("skin") String skin, final @Argument("name") String name, @Argument("nickname") String nickname) {
        UUID uuid = p.getUniqueId();
        Audience target = getPlugin().getBukkitAudiences().player(p);
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (whetherServerEnableAutoSync2()) {
            MiscMessage.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
            return;
        }
        Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK);
        Map<String, String> ranks_permission = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PERMISSION);
        Map<String, String> ranks_prefix = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PREFIX);
        Map<String, String> sign = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_SIGN);
        boolean custom = p.hasPermission("floracore.command.nick.custom");
        if (rank != null) {
            if (ranks.containsKey(rank)) {
                String permission = ranks_permission.get(rank);
                if (!p.hasPermission(permission)) {
                    Message.COMMAND_MISC_NICK_RANK_NO_PERMISSION.send(sender, rank);
                    return;
                }
            } else {
                Message.COMMAND_MISC_NICK_RANK_UNKNOWN.send(sender, rank);
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
                    Message.COMMAND_NICK_SETUP_SKIN.send(sender);
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
                        performNick(p, rank, skin, nickname, true);
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
                                performNick(p, rank, skin, nickname, true);
                                target.openBook(getFinishPage(ranks_prefix.get(rank), nickname, uuid));
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
                    String line1 = sign.get("line" + 1);
                    String line2 = sign.get("line" + 2);
                    String line3 = sign.get("line" + 3);
                    String line4 = sign.get("line" + 4);
                    SignGUIAPI signGUIAPI = new SignGUIAPI(event -> {
                        String i = event.getLines().get(0);
                        int nameLengthMin = Math.min(3, 16), nameLengthMax = Math.max(16, 1);
                        if (!(i.isEmpty()) && (i.length() <= nameLengthMax) && (i.length() >= nameLengthMin)) {
                            performNick(p, rank, skin, i, true);
                            target.openBook(getFinishPage(ranks_prefix.get(rank), i, uuid));
                        }
                    }, Arrays.asList(line1, line2, line3, line4), uuid, getPlugin().getBootstrap().getPlugin());
                    signGUIAPI.open();
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
        }
    }

    private void performUnNick(Player p) {
        UUID uuid = p.getUniqueId();
        DATA statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        // 获取是否已经Nick
        if (!whetherServerEnableAutoSync2()) {
            nickedPlayers.remove(uuid);
        }
        // 清除数据库Nick状态
        getAsyncExecutor().execute(() -> {
            DATA rankData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.rank");
            DATA lpPrefixData = getStorageImplementation().getSpecifiedData(uuid, DataType.STAGING_DATA, "luckperms.prefix");
            if (statusData != null) {
                getStorageImplementation().deleteDataID(statusData.getId());
            }
            if (rankData != null) {
                getStorageImplementation().deleteDataID(rankData.getId());
            }
            if (lpPrefixData != null) {
                // 恢复LP的Rank状态
                /*User user = luckPerms.getUserManager().getUser(uuid);
                if (user != null) {
                    PrefixNode node = PrefixNode.builder(lpPrefixData.getValue(), 100).build();
                    user.data().add(node);
                    luckPerms.getUserManager().saveUser(user);
                    getStorageImplementation().deleteDataID(lpPrefixData.getId());
                }*/
            }
        });
    }

    private void performNick(Player p, String rank, String skin, String name, boolean typeNick) {
        UUID uuid = p.getUniqueId();
        // 获取是否已经Nick
        DATA statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        if (statusData != null && Boolean.parseBoolean(statusData.getValue())) {
            performUnNick(p);
        }
        if (whetherServerEnableAutoSync2()) {
            // 修改玩家信息
            changePlayer(p, name);
        } else {
            nickedPlayers.add(uuid);
        }
        // 设置皮肤
        String skinName;
        if (typeNick) {
            if (skin.equalsIgnoreCase("normal")) {
                skinName = uuid.toString();
            } else if (skin.equalsIgnoreCase("steve-alex")) {
                Random random = new Random();
                int randomNum = random.nextInt(2);
                skinName = randomNum == 0 ? "Steve" : "Alex";
            } else {
                skinName = getPlugin().getNamesRepository().getRandomNameProperty().getName();
            }
        } else {
            skinName = skin;
        }
        if (whetherServerEnableAutoSync2()) {
            try {
                // 判断是否为Normal，如果为Normal则不进行操作
                UUID i = UUID.fromString(skinName);
            } catch (IllegalArgumentException exception) {
                NamesRepository.NameProperty selectedSkin;
                // 不是Normal
                // 判断是不是Steve/Alex
                if (skinName.equalsIgnoreCase("Steve")) {
                    selectedSkin = getPlugin().getNamesRepository().getSteveProperty();
                } else if (skinName.equalsIgnoreCase("Alex")) {
                    selectedSkin = getPlugin().getNamesRepository().getAlexProperty();
                } else {
                    selectedSkin = getPlugin().getNamesRepository().getNameProperty(skinName);
                }
                if (selectedSkin != null) {
                    // 设置玩家皮肤
                    /*getAsyncExecutor().execute(() -> {
                        IProperty iProperty = skinsRestorerAPI.createPlatformProperty(selectedSkin.getName(), selectedSkin.getValue(), selectedSkin.getSignature());
                        skinsRestorerAPI.applySkin(new PlayerWrapper(p), iProperty);
                    });*/
                }
            }
        }
        // 设置Rank
        /*User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            Data lpPrefixData = getStorageImplementation().getSpecifiedData(uuid, DataType.STAGING_DATA, "luckperms.prefix");
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (lpPrefixData != null) {
                if (whetherServerEnableAutoSync2()) {
                    PrefixNode node = PrefixNode.builder(lpPrefixData.getValue(), 100).build();
                    user.data().add(node);
                    luckPerms.getUserManager().saveUser(user);
                } else {
                    getAsyncExecutor().execute(() -> {
                        getStorageImplementation().insertData(uuid, DataType.FUNCTION, "luckperms.prefix", prefix, 0);
                    });
                }
            }
        }*/

        // 设置数据库Nick状态
        getAsyncExecutor().execute(() -> {
            getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.skin", skinName, 0);
            getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.name", name, 0);
            getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.rank", rank, 0);
            getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.status", String.valueOf(true), 0);
        });
    }

    /**
     * 修改玩家的名字
     */
    public void changePlayer(Player p, String name) {
        String on = p.getName();
        Object NMSPlayer = invokeMethod(getHandle, p);
        Class<?> action = getInnerClass(getNMSClass("PacketPlayOutPlayerInfo"), "EnumPlayerInfoAction");
        Object ps = Array.newInstance(NMSPlayer.getClass(), 1);
        Array.set(ps, 0, NMSPlayer);
        Object pack = newInstance(getConstructor(getNMSClass("PacketPlayOutPlayerInfo"), action, ps.getClass()), Enum.valueOf((Class) action, "REMOVE_PLAYER"), ps);
        sendPacketToAllPlayers(pack);
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutEntityDestroy"), int[].class), new int[]{p.getEntityId()});
        sendPacketToAllPlayersWhich(pack, p2 -> p2 != p && !p2.equals(p) && p2.canSee(p));
        setFieldValue(getField(NMSPlayer.getClass(), "displayName"), NMSPlayer, name);
        Object n2 = invokeMethod(getMethod(getInnerClass(getNMSClass("IChatBaseComponent"), "ChatSerializer"), "a", String.class), null, "{\"text\":\"" + name.replace("\"", "\\\"") + "\"}");
        setFieldValue(getField(NMSPlayer.getClass(), "listName"), NMSPlayer, n2);
        Object gameProfile = invokeMethod(getMethod(p.getClass(), "getProfile"), p);
        setFieldValue(getField(gameProfile.getClass(), "name"), gameProfile, name);
        Object NMSServer = getFieldValue(getField(Bukkit.getServer().getClass(), "console"), Bukkit.getServer());
        assert NMSServer != null;
        Object pl = invokeMethod(getMethod(NMSServer.getClass(), "getPlayerList"), NMSServer);
        Map<String, Object> players = getFieldValue(getField(getNMSClass("PlayerList"), "playersByName"), pl);
        assert players != null;
        players.remove(on);
        players.put(name, NMSPlayer);
        setFieldValue(getField(getNMSClass("PlayerList"), "playersByName"), pl, players);
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutPlayerInfo"), action, ps.getClass()), Enum.valueOf((Class) action, "ADD_PLAYER"), ps);
        sendPacketToAllPlayers(pack);
        invokeMethod(getMethod(NMSPlayer.getClass(), "updateAbilities"), NMSPlayer);
        Location l = p.getLocation();
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutPosition"), double.class, double.class, double.class, float.class, float.class, Set.class), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<Enum<?>>());
        sendPacketToAllPlayersWhich(pack, p2 -> p2 == p || p2.equals(p));
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutHeldItemSlot"), int.class), p.getInventory().getHeldItemSlot());
        sendPacketToAllPlayersWhich(pack, p2 -> p2 == p || p2.equals(p));
        invokeMethod(getMethod(p.getClass(), "updateScaledHealth"), p);
        invokeMethod(getMethod(p.getClass(), "updateInventory"), p);
        invokeMethod(getMethod(NMSPlayer.getClass(), "triggerHealthUpdate"), NMSPlayer);
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutEntityEquipment"), int.class, int.class, getNMSClass("ItemStack")), p.getEntityId(), 0, invokeMethod(getMethod(getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), null, Inventories.getItemInMainHand(p)));
        sendPacketToAllPlayersWhich(pack, p2 -> p2 != p && !p2.equals(p) && p2.canSee(p));
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutEntityEquipment"), int.class, int.class, getNMSClass("ItemStack")), p.getEntityId(), 4, invokeMethod(getMethod(getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), null, p.getInventory().getHelmet()));
        sendPacketToAllPlayersWhich(pack, p2 -> p2 != p && !p2.equals(p) && p2.canSee(p));
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutEntityEquipment"), int.class, int.class, getNMSClass("ItemStack")), p.getEntityId(), 3, invokeMethod(getMethod(getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), null, p.getInventory().getChestplate()));
        sendPacketToAllPlayersWhich(pack, p2 -> p2 != p && !p2.equals(p) && p2.canSee(p));
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutEntityEquipment"), int.class, int.class, getNMSClass("ItemStack")), p.getEntityId(), 2, invokeMethod(getMethod(getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), null, p.getInventory().getLeggings()));
        sendPacketToAllPlayersWhich(pack, p2 -> p2 != p && !p2.equals(p) && p2.canSee(p));
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutEntityEquipment"), int.class, int.class, getNMSClass("ItemStack")), p.getEntityId(), 1, invokeMethod(getMethod(getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), null, p.getInventory().getBoots()));
        sendPacketToAllPlayersWhich(pack, p2 -> p2 != p && !p2.equals(p) && p2.canSee(p));
        pack = newInstance(getConstructor(getNMSClass("PacketPlayOutNamedEntitySpawn"), getNMSClass("EntityHuman")), NMSPlayer);
        sendPacketToAllPlayersWhich(pack, p2 -> p2 != p && !p2.equals(p) && p2.canSee(p));
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
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        DATA data = storageImplementation.getSpecifiedData(uuid, DataType.FUNCTION, "nick.skin");
        if (data != null) {
            // reuse skin
            Component reuse = TranslationManager.render(BookMessage.COMMAND_MISC_NICK_BOOK_SKIN_PAGE_REUSE.build(rank, data.getValue()), uuid);
            component = join(joinConfig, component, reuse);
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

    private Book getRandomPage(String rank, String skin, UUID uuid) {
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID u = p.getUniqueId();
        DATA data = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.status");
        if (data != null) {
            String value = data.getValue();
            boolean nick = Boolean.parseBoolean(value);
            if (nick && p.hasPermission("floracore.command.nick")) {
                if (whetherServerEnableAutoSync2()) {
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
                } else {
                    nickedPlayers.add(u);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID u = p.getUniqueId();
        if (whetherServerEnableAutoSync2()) {

        } else {
            nickedPlayers.remove(u);
        }
    }
}
