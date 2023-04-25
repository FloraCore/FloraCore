package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import net.kyori.adventure.audience.*;
import net.kyori.adventure.inventory.*;
import net.kyori.adventure.text.*;
import net.skinsrestorer.api.*;
import net.skinsrestorer.api.property.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.config.*;
import team.floracore.common.locale.*;
import team.floracore.common.locale.data.*;
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

@CommandPermission("floracore.command.nick")
@Deprecated
public class OldNickCommand extends AbstractFloraCoreCommand implements Listener {
    private final SkinsRestorerAPI skinsRestorerAPI;
    private final Set<UUID> nickedPlayers = new HashSet<>();

    public OldNickCommand(FloraCorePlugin plugin) {
        super(plugin);
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
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
            Message.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
            return;
        }
        bookNick(p, 0, null, null, null, null);
    }

    @CommandMethod("nick <name>")
    @CommandDescription("将你的昵称修改为一个指定的昵称")
    @CommandPermission("floracore.command.nick.custom")
    public void nickSpecifiedName(final @NotNull Player p, final @Argument("name") String name) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (whetherServerEnableAutoSync2()) {
            Message.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
            return;
        }
        Audience target = getPlugin().getBukkitAudiences().player(p);
        performNick(p, "rank0", "random", name);
        target.openBook(getFinishPage("rank0", name));
    }

    @CommandMethod("unnick")
    @CommandDescription("取消你的昵称")
    public void unNick(final @NotNull Player p) {
        UUID uuid = p.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        if (whetherServerEnableAutoSync2()) {
            Message.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
            return;
        }
        Data statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
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
            Message.COMMAND_CURRENT_SERVER_FORBIDDEN.send(sender);
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
                        if (name.equalsIgnoreCase("random") && custom) {
                            target.openBook(getRandomPage(rank, skin));
                            return;
                        }
                        if (name.equalsIgnoreCase("reuse")) {
                            Data data = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.name");
                            nickname = (data != null) ? data.getValue() : getPlugin().getNamesRepository().getRandomNameProperty().getName();
                        } else if (name.equalsIgnoreCase("random") && !custom) {
                            nickname = getPlugin().getNamesRepository().getRandomNameProperty().getName();
                        }
                        performNick(p, rank, skin, nickname);
                        target.openBook(getFinishPage(ranks_prefix.get(rank), nickname));
                    }
                    break;
                case 5:
                    // random nick
                    if (custom) {
                        if (name.equalsIgnoreCase("random")) {
                            if (nickname == null) {
                                Message.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
                            } else {
                                performNick(p, rank, skin, nickname);
                                target.openBook(getFinishPage(ranks_prefix.get(rank), nickname));
                            }
                        } else {
                            Message.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
                        }
                    } else {
                        Message.COMMAND_NO_PERMISSION.send(sender);
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
                            performNick(p, rank, skin, i);
                            target.openBook(getFinishPage(ranks_prefix.get(rank), i));
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

    private void performUnNick(Player p) {
        UUID uuid = p.getUniqueId();
        Data statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        // 获取是否已经Nick
        if (whetherServerEnableAutoSync2()) {
            if (statusData != null && Boolean.parseBoolean(statusData.getValue())) {
                Players ps = getStorageImplementation().selectPlayers(uuid);
                // 重置玩家信息
                changePlayer(p, ps.getName());
                // 设置皮肤
                getAsyncExecutor().execute(() -> {
                    Data skinData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.skin");
                    if (skinData != null) {
                        skinsRestorerAPI.removeSkin(p.getName());
                    }
                });
            }
        } else {
            nickedPlayers.remove(uuid);
        }
        // 清除数据库Nick状态
        getAsyncExecutor().execute(() -> {
            Data rankData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.rank");
            if (statusData != null) {
                getStorageImplementation().deleteDataID(statusData.getId());
            }
            if (rankData != null) {
                getStorageImplementation().deleteDataID(rankData.getId());
            }
        });
    }

    private void performNick(Player p, String rank, String skin, String name) {
        UUID uuid = p.getUniqueId();
        // 获取是否已经Nick
        Data statusData = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.status");
        if (statusData != null && Boolean.parseBoolean(statusData.getValue())) {
            performUnNick(p);
        }
        boolean changeSkin = !skin.equalsIgnoreCase("normal");
        if (whetherServerEnableAutoSync2()) {
            // 修改玩家信息
            changePlayer(p, name);
        } else {
            nickedPlayers.add(uuid);
        }
        // 设置皮肤
        NamesRepository.NameProperty selectedSkin;
        if (changeSkin) {
            if (skin.equalsIgnoreCase("steve-alex")) {
                Random random = new Random();
                // 生成 0 或 1 的随机数
                int randomNum = random.nextInt(2);
                // 如果随机数为 0，选择 Steve 皮肤属性，否则选择 Alex 皮肤属性
                selectedSkin = (randomNum == 0) ? getPlugin().getNamesRepository().getSteveProperty() : getPlugin().getNamesRepository().getAlexProperty();
                getAsyncExecutor().execute(() -> {
                    getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.skin", randomNum == 0 ? "Steve" : "Alex", 0);
                });
            } else {
                selectedSkin = getPlugin().getNamesRepository().getRandomNameProperty();
                getAsyncExecutor().execute(() -> {
                    getStorageImplementation().insertData(uuid, DataType.FUNCTION, "nick.skin", selectedSkin.getName(), 0);
                });
            }
            // 设置皮肤
            if (whetherServerEnableAutoSync2()) {
                getAsyncExecutor().execute(() -> {
                    IProperty iProperty = skinsRestorerAPI.createPlatformProperty(selectedSkin.getName(), selectedSkin.getValue(), selectedSkin.getSignature());
                    skinsRestorerAPI.applySkin(new PlayerWrapper(p), iProperty);
                });
            }
        }
        // 设置数据库Nick状态
        getAsyncExecutor().execute(() -> {
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
        Data data = storageImplementation.getSpecifiedData(uuid, DataType.FUNCTION, "nick.skin");
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
        Data data = getStorageImplementation().getSpecifiedData(uuid, DataType.FUNCTION, "nick.name");
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

    private Book getRandomPage(String rank, String skin) {
        Component bookTitle = text("FloraCore Nick RandomPage");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        String nickname = getPlugin().getNamesRepository().getRandomNameProperty().getName();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component component = join(joinConfig, Message.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_LINE_1.build(),
                // name
                Message.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_NAME.build(nickname), space(),
                // use
                Message.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_USE_NAME.build(rank, skin, nickname),
                // try
                Message.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_TRY_AGAIN.build(rank, skin), space(),
                // custom
                Message.COMMAND_MISC_NICK_BOOK_RANDOM_PAGE_CUSTOM.build(rank, skin)).asComponent();
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID u = p.getUniqueId();
        Data data = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.status");
        if (data != null) {
            String value = data.getValue();
            boolean nick = Boolean.parseBoolean(value);
            if (nick && p.hasPermission("floracore.command.nick")) {
                if (whetherServerEnableAutoSync2()) {
                    Map<String, String> ranks = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK);
                    Map<String, String> ranks_permission = getPlugin().getConfiguration().get(ConfigKeys.COMMANDS_NICK_RANK_PERMISSION);
                    Data rankData = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.rank");
                    Data skinData = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.skin");
                    Data nameData = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "nick.name");
                    if (rankData != null && skinData != null && nameData != null) {
                        String rank = rankData.getValue();
                        String skin = skinData.getValue();
                        String name = nameData.getValue();
                        if (rank != null && skin != null && name != null) {
                            if (ranks.containsKey(rank)) {
                                String permission = ranks_permission.get(rank);
                                if (p.hasPermission(permission)) {
                                    performNick(p, rank, skin, name);
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
