package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import de.myzelyam.api.vanish.*;
import net.kyori.adventure.audience.*;
import net.kyori.adventure.inventory.*;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.floracore.api.commands.report.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.floracore.api.messenger.message.type.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.inevntory.*;
import team.floracore.common.inevntory.content.*;
import team.floracore.common.locale.data.chat.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;
import team.floracore.common.util.builder.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static net.kyori.adventure.text.Component.*;
import static team.floracore.common.util.ReflectionWrapper.*;

/**
 * Report命令
 */
@CommandPermission("floracore.command.report")
@CommandDescription("举报一名玩家")
public class ReportCommand extends AbstractFloraCoreCommand {
    public static final boolean ADVANCED_VERSION = isVersionGreaterThanOrEqual(getVersion(), "v1_13_R1");
    private final List<REPORT> reports = new ArrayList<>();

    public ReportCommand(FloraCorePlugin plugin) {
        super(plugin);
        reports.addAll(getStorageImplementation().getReports());
        plugin.getBootstrap().getScheduler().asyncRepeating(() -> {
            reports.clear();
            reports.addAll(getStorageImplementation().getReports());
        }, 3, TimeUnit.SECONDS);
    }

    public static String joinList(List<String> list, int number) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(list.size(), number); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(list.get(i));
        }
        if (list.size() > number) {
            sb.append(", ...");
        }
        return sb.toString();
    }

    @CommandMethod("report-tp <target>")
    @CommandPermission("floracore.command.report.staff")
    public void reportTeleport(final @NotNull Player sender, final @Argument("target") String target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Message.COMMAND_REPORT_TP_TRANSMITTING.send(s);
        UUID u = sender.getUniqueId();
        UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
        if (ut == null) {
            MiscMessage.PLAYER_NOT_FOUND.send(s, target);
            return;
        }
        String server;
        DATA data = getStorageImplementation().getSpecifiedData(ut, DataType.FUNCTION, "server-status");
        if (data != null) {
            server = data.getValue();
        } else {
            MiscMessage.PLAYER_NOT_FOUND.send(s, target);
            return;
        }
        if (getPlugin().getServerName().equalsIgnoreCase(server)) {
            Player t = Bukkit.getPlayer(target);
            if (t != null) {
                if (getPlugin().isPluginInstalled("PremiumVanish")) {
                    if (!VanishAPI.isInvisible(sender)) {
                        VanishAPI.hidePlayer(sender);
                    }
                }
                sender.teleport(t.getLocation());
                Message.COMMAND_REPORT_TP_SUCCESS.send(s, target);
            } else {
                MiscMessage.PLAYER_NOT_FOUND.send(s, target);
            }
        } else {
            getPlugin().getMessagingService().ifPresent(service -> service.pushTeleport(u, ut, server));
            getPlugin().getBungeeUtil().connect(sender, server);
        }
    }

    @CommandMethod("report <target> <reason>")
    public void report(final @NotNull Player sender, final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NotNull @Argument("reason") @Greedy String reason) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player t = Bukkit.getPlayer(target);
        UUID reportedUser;
        if (t != null) {
            if (t.getUniqueId() == sender.getUniqueId()) {
                Message.COMMAND_REPORT_SELF.send(s);
                return;
            }
            reportedUser = t.getUniqueId();
        } else {
            UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
            if (ut == null) {
                MiscMessage.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
            if (hasPermission(ut, "floracore.command.report.bypass")) {
                Message.COMMAND_REPORT_NOT_PERMISSION.send(s);
                return;
            }
            reportedUser = ut;
        }
        final String reporterServer = getPlugin().getServerName();
        DATA data = getStorageImplementation().getSpecifiedData(reportedUser, DataType.FUNCTION, "server-status");
        final String reportedUserServer;
        if (data != null) {
            reportedUserServer = data.getValue();
        } else {
            Message.COMMAND_REPORT_ABNORMAL.send(s);
            return;
        }
        REPORT report = getStorageImplementation().getUnprocessedReports(reportedUser);
        if (report != null) {
            if (report.getReporters().contains(s.getUniqueId())) {
                Message.COMMAND_REPORT_REPEAT.send(s);
                return;
            }
        }
        Message.COMMAND_REPORT_SUCCESS.send(s, target, reason);
        createReport(s.getUniqueId(), reportedUser, reporterServer, reportedUserServer, reason);
    }

    @CommandMethod("reports")
    @CommandPermission("floracore.command.report.staff")
    public void reports(final @NotNull Player player) {
        Sender s = getPlugin().getSenderFactory().wrap(player);
        getReportsMainGui(player, false).open(player);
    }

    @CommandMethod("reports-chats|rcs <uuid> <conclusion>")
    @CommandPermission("floracore.command.report.staff")
    public void reportsChats(final @NotNull Player player, final @NotNull @Argument("uuid") UUID uuid, final @NotNull @Argument("conclusion") Boolean conclusion) {
        getChatsGUI(player, uuid, conclusion).open(player);
    }

    private void createReport(UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer, String reason) {
        getPlugin().getMessagingService().ifPresent(service -> {
            UUID uuid = UUID.randomUUID();
            long time = System.currentTimeMillis();
            List<ReportDataChatRecord> chat = new ArrayList<>();
            ChatAPI chatAPI = getPlugin().getApiProvider().getChatAPI();
            ChatManager chatManager = getPlugin().getChatManager();
            List<ReportDataChatRecord> c1 = getPlayerChatUUIDRecent(reporter, time, chatAPI, chatManager);
            List<ReportDataChatRecord> c2 = getPlayerChatUUIDRecent(reportedUser, time, chatAPI, chatManager);
            chat.addAll(c1);
            chat.addAll(c2);
            getStorageImplementation().addReport(uuid, reporter, reportedUser, reason, time, chat);
            service.pushReport(reporter, reportedUser, reporterServer, reportedUserServer, reason);
        });
    }

    private List<ReportDataChatRecord> getPlayerChatUUIDRecent(UUID reporter, long time, ChatAPI chatAPI, ChatManager chatManager) {
        List<ReportDataChatRecord> c = chatAPI.getPlayerChatUUIDRecent(reporter, 3)
                .stream()
                .map(dataChatRecord -> new ReportDataChatRecord(reporter, dataChatRecord))
                .collect(Collectors.toList());
        ChatManager.MapPlayerRecord cm1 = chatManager.getMapPlayerRecord(reporter);
        if (cm1 != null) {
            int id = chatManager.getChat().getId();
            DataChatRecord d = new DataChatRecord(id, cm1.getJoinTime(), time);
            c.add(new ReportDataChatRecord(reporter, d));
        }
        return c;
    }

    private SmartInventory getReportsMainGui(Player player, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        Component title;
        List<REPORT> filteredReports;
        if (conclusion) {
            filteredReports = reports.stream()
                    .filter(report -> report.getStatus() == ReportStatus.ENDED)
                    .sorted(Comparator.comparingInt(REPORT::getId))
                    .collect(Collectors.toList());
            title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_TITLE.build(), uuid).append(space()).append(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_PROCESSED.build(), uuid));
        } else {
            filteredReports = reports.stream()
                    .filter(report -> report.getStatus() != ReportStatus.ENDED)
                    .sorted(Comparator.comparingInt(REPORT::getId))
                    .collect(Collectors.toList());
            title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_TITLE.build(), uuid);
        }
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(title);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ClickableItem[] items = new ClickableItem[filteredReports.size()];
            for (int i = 0; i < items.length; i++) {
                REPORT report = filteredReports.get(i);
                int id = report.getId();
                Component rt = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_TITLE.build(id), uuid);
                List<Component> lore = getReportLore(report, uuid);
                lore.add(Component.space());
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid));
                ItemBuilder ri = new ItemBuilder(Material.PAPER).displayName(rt).lore(lore);
                if (report.getStatus() == ReportStatus.ACCEPTED) {
                    ri.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).flags(ItemFlag.HIDE_ENCHANTS);
                }
                items[i] = ClickableItem.of(ri.build(), inventoryClickEvent -> getReportGui(player, report.getUniqueId(), conclusion).open(player));
            }
            pagination.setItems(items);
            pagination.setItemsPerPage(27);
            Component t = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_PAGE.build(pagination.getPage() + 1), uuid);
            contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.BOOKSHELF).displayName(title).lore(t).build()));
            int i = 18;
            for (ClickableItem pageItem : pagination.getPageItems()) {
                i++;
                contents.set(SmartInventory.getInventoryRow(i), SmartInventory.getInventoryColumn(i), pageItem);
            }
            supplementaryMenu(contents);
            if (conclusion) {
                Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
                contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(back).build(), event -> getReportsMainGui(player, false).open(player)));
            } else {
                Component t1 = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_PROCESSED.build(), uuid);
                contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.CHEST).displayName(t1).build(), inventoryClickEvent -> getReportsMainGui(player, true).open(player)));
            }
            setPageSlot(player, conclusion, uuid, contents, pagination);
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).displayName(close).build(), event -> player.closeInventory()));
        });
        return builder.build();
    }

    private void setPageSlot(Player player, boolean conclusion, UUID uuid, InventoryContents contents, Pagination pagination) {
        if (!pagination.isFirst()) {
            Component previous = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(), uuid);
            Component turn = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage()), uuid);
            contents.set(5, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(previous).lore(turn).build(), event -> getReportsMainGui(player, conclusion).open(player, pagination.previous().getPage())));
        }
        if (!pagination.isLast()) {
            Component next = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
            Component turn = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage() + 2), uuid);
            contents.set(5, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(next).lore(turn).build(), event -> getReportsMainGui(player, conclusion).open(player, pagination.next().getPage())));
        }
    }

    private SmartInventory getReportGui(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space()).append(Message.ARROW.color(NamedTextColor.GRAY)).append(Component.space()).append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(finalTitle);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            ItemBuilder i1 = new ItemBuilder(Material.PAPER).displayName(finalTitle).lore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).flags(ItemFlag.HIDE_ENCHANTS);
            contents.set(0, 4, ClickableItem.empty(i1.build()));
            String resultRns = getReports(report);
            ItemStack rs = getPlayerItemBuilder(report.getReporters().get(0)).displayName(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER.build(resultRns), uuid)).lore(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid)).build();
            contents.set(2, 3, ClickableItem.of(rs, inventoryClickEvent -> getReportersGUI(player, reportUUID, conclusion).open(player)));
            String r1 = getPlayerRecordName(report.getReported());
            if (r1 == null) {
                r1 = "UNKNOWN";
            }
            boolean online = isOnline(report.getReported());
            ItemBuilder rds = getPlayerItemBuilder(report.getReported()).displayName(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTED.build(r1, online), uuid));
            if (online) {
                rds.lore(TranslationManager.render(MiscMessage.CHECK_TP.build(), uuid));
                String finalR = r1;
                contents.set(2, 5, ClickableItem.of(rds.build(), inventoryClickEvent -> {
                    reportTeleport(player, finalR);
                    player.closeInventory();
                }));
            } else {
                contents.set(2, 5, ClickableItem.empty(rds.build()));
            }
            ItemStack chats = new ItemBuilder(Material.BOOKSHELF).displayName(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_CHAT.build(), uuid)).lore(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid)).build();
            contents.set(2, 4, ClickableItem.of(chats, inventoryClickEvent -> getChatsGUI(player, reportUUID, conclusion).open(player)));
            switch (report.getStatus()) {
                case WAITING:
                    Component accepted = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_ACCEPTED.build(), uuid);
                    ItemStack ai;
                    if (ADVANCED_VERSION) {
                        ai = new ItemBuilder(Material.LIME_TERRACOTTA).displayName(accepted).build();
                    } else {
                        ai = new ItemBuilder(new ItemStack(Material.matchMaterial("STAINED_CLAY"), 1, (short) 5)).displayName(accepted).build();
                    }
                    contents.set(3, 4, ClickableItem.of(ai, inventoryClickEvent -> {
                        report.setStatus(ReportStatus.ACCEPTED);
                        getReportGui(player, reportUUID, conclusion).open(player);
                        getPlugin().getMessagingService().ifPresent(service -> {
                            String reported = getPlayerRecordName(report.getReported());
                            for (UUID reporter : report.getReporters()) {
                                service.pushNoticeMessage(reporter, NoticeMessage.NoticeType.REPORT_ACCEPTED, new String[]{reported});
                            }
                            service.pushNoticeMessage(UUID.randomUUID(), NoticeMessage.NoticeType.REPORT_STAFF_ACCEPTED, new String[]{resultRns, reported});
                        });
                    }));
                    break;
                case ACCEPTED:
                    Component end = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_END.build(), uuid);
                    ItemBuilder ei;
                    if (ADVANCED_VERSION) {
                        ei = new ItemBuilder(Material.LIGHT_BLUE_TERRACOTTA).displayName(end);
                    } else {
                        ei = new ItemBuilder(new ItemStack(Material.matchMaterial("STAINED_CLAY"), 1, (short) 3)).displayName(end);
                    }
                    ei.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).flags(ItemFlag.HIDE_ENCHANTS);
                    contents.set(3, 4, ClickableItem.of(ei.build(), inventoryClickEvent -> {
                        report.setStatus(ReportStatus.ENDED);
                        report.setConclusionTime(System.currentTimeMillis());
                        getReportGui(player, reportUUID, conclusion).open(player);
                        getPlugin().getMessagingService().ifPresent(service -> {
                            String reported = getPlayerRecordName(report.getReported());
                            for (UUID reporter : report.getReporters()) {
                                service.pushNoticeMessage(reporter, NoticeMessage.NoticeType.REPORT_PROCESSED, new String[]{reported});
                            }
                            service.pushNoticeMessage(UUID.randomUUID(), NoticeMessage.NoticeType.REPORT_STAFF_PROCESSED, new String[]{resultRns, reported});
                        });
                    }));
                    break;
                case ENDED:
                    Component ended = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_ENDED.build(), uuid);
                    ItemStack edi;
                    if (ADVANCED_VERSION) {
                        edi = new ItemBuilder(Material.RED_TERRACOTTA).displayName(ended).build();
                    } else {
                        edi = new ItemBuilder(new ItemStack(Material.matchMaterial("STAINED_CLAY"), 1, (short) 14)).displayName(ended).build();
                    }
                    contents.set(3, 4, ClickableItem.empty(edi));
                    break;
            }
            supplementaryMenu(contents);
            Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
            contents.set(5, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(back).build(), event -> getReportsMainGui(player, conclusion).open(player)));
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).displayName(close).build(), event -> player.closeInventory()));
        });
        return builder.build();
    }

    private SmartInventory getReportersGUI(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space()).append(Message.ARROW.color(NamedTextColor.GRAY)).append(Component.space()).append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(finalTitle);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ItemBuilder i1 = new ItemBuilder(Material.PAPER).displayName(finalTitle).lore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).flags(ItemFlag.HIDE_ENCHANTS);
            contents.set(0, 4, ClickableItem.empty(i1.build()));
            List<UUID> reporters = report.getReporters();
            ClickableItem[] items = new ClickableItem[reporters.size()];
            for (int i = 0; i < items.length; i++) {
                String r1 = getPlayerRecordName(reporters.get(i));
                if (r1 == null) {
                    r1 = "UNKNOWN";
                }
                boolean online = isOnline(reporters.get(i));
                ItemBuilder rds = getPlayerItemBuilder(reporters.get(i)).displayName(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER_DETAILED.build(r1, online), uuid));
                if (online) {
                    rds.lore(TranslationManager.render(MiscMessage.CHECK_TP.build(), uuid));
                    String finalR = r1;
                    items[i] = ClickableItem.of(rds.build(), inventoryClickEvent -> {
                        reportTeleport(player, finalR);
                        player.closeInventory();
                    });
                } else {
                    items[i] = ClickableItem.empty(rds.build());
                }
            }
            pagination.setItems(items);
            pagination.setItemsPerPage(27);
            int i = 18;
            for (ClickableItem pageItem : pagination.getPageItems()) {
                i++;
                contents.set(SmartInventory.getInventoryRow(i), SmartInventory.getInventoryColumn(i), pageItem);
            }
            supplementaryMenu(contents);
            setPageSlot(player, conclusion, uuid, contents, pagination);
            Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
            contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(back).build(), event -> getReportGui(player, reportUUID, conclusion).open(player)));
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).displayName(close).build(), event -> player.closeInventory()));
        });
        return builder.build();
    }

    private SmartInventory getChatsGUI(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        Audience target = getPlugin().getBukkitAudiences().player(player);
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_CHATS_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space()).append(Message.ARROW.color(NamedTextColor.GRAY)).append(Component.space()).append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(finalTitle);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ItemBuilder i1 = new ItemBuilder(Material.PAPER).displayName(finalTitle).lore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).flags(ItemFlag.HIDE_ENCHANTS);
            contents.set(0, 4, ClickableItem.empty(i1.build()));
            List<ReportDataChatRecord> chats = report.getChat();
            ClickableItem[] items = new ClickableItem[chats.size()];
            for (int i = 0; i < items.length; i++) {
                ReportDataChatRecord chat = chats.get(i);
                ItemBuilder item = new ItemBuilder(Material.BOOK);
                int id = chat.getDataChatRecord().getId();
                Component ct = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_TITLE.build(id), uuid);
                item.displayName(ct);
                List<Component> lore = new ArrayList<>();
                lore.add(Component.space());
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_PLAYER.build(getPlayerRecordName(chat.getUuid())), uuid));
                long startTime = chat.getDataChatRecord().getJoinTime();
                long endTime = chat.getDataChatRecord().getQuitTime();
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_START_TIME.build(DurationFormatter.getTimeFromTimestamp(startTime)), uuid));
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_END_TIME.build(DurationFormatter.getTimeFromTimestamp(endTime)), uuid));
                lore.add(Component.space());
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid));
                item.lore(lore);
                items[i] = ClickableItem.of(item.build(), inventoryClickEvent -> target.openBook(getChatPage(uuid, reportUUID, chat, conclusion)));
            }
            pagination.setItems(items);
            pagination.setItemsPerPage(27);
            int i = 18;
            for (ClickableItem pageItem : pagination.getPageItems()) {
                i++;
                contents.set(SmartInventory.getInventoryRow(i), SmartInventory.getInventoryColumn(i), pageItem);
            }
            supplementaryMenu(contents);
            setPageSlot(player, conclusion, uuid, contents, pagination);
            Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
            contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(back).build(), event -> getReportGui(player, reportUUID, conclusion).open(player)));
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).displayName(close).build(), event -> player.closeInventory()));
        });
        return builder.build();
    }

    private ItemBuilder getPlayerItemBuilder(UUID uuid) {
        Material sm;
        if (ADVANCED_VERSION) {
            sm = Material.PLAYER_HEAD;
        } else {
            sm = Material.matchMaterial("SKULL_ITEM");
        }
        ItemBuilder ib = new ItemBuilder(sm).setSkullOwner(getPlayerRecordName(uuid));
        if (!ADVANCED_VERSION) {
            ib.durability((short) 3);
        }
        return ib;
    }

    private String getReports(REPORT report) {
        List<String> rns = new ArrayList<>();
        for (UUID reporter : report.getReporters()) {
            String name = getPlayerRecordName(reporter);
            if (name != null) {
                rns.add(name);
            }
        }
        return joinList(rns, 3);
    }

    private void supplementaryMenu(InventoryContents contents) {
        for (int j = 0; j < 9; j++) {
            ItemStack empty;
            if (ADVANCED_VERSION) {
                empty = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName(Component.space()).build();
            } else {
                empty = new ItemBuilder(new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 7)).displayName(Component.space()).build();
            }
            contents.set(1, j, ClickableItem.empty(empty));
            contents.set(5, j, ClickableItem.empty(empty));
        }
    }

    private List<Component> getReportLore(REPORT report, UUID uuid) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.space());
        switch (report.getStatus()) {
            case WAITING:
                Component waiting = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_WAITING.build(), uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(waiting), uuid));
                break;
            case ACCEPTED:
                Component accepted = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_ACCEPTED.build(), uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(accepted), uuid));
                break;
            case ENDED:
                Component ended = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_ENDED.build(), uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(ended), uuid));
                break;
        }
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_TIME.build(DurationFormatter.getTimeFromTimestamp(report.getReportTime())), uuid));
        lore.add(Component.space());
        String resultRns = getReports(report);
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER.build(resultRns), uuid));
        String reported = getPlayerRecordName(report.getReported());
        if (reported == null) {
            reported = "UNKNOWN";
        }
        boolean online = isOnline(report.getReported());
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTED.build(reported, online), uuid));
        String resultReason = joinList(report.getReasons(), 2);
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REASON.build(resultReason), uuid));
        return lore;
    }


    private Book getChatPage(UUID uuid, UUID reportUUID, ReportDataChatRecord reportDataChatRecord, boolean conclusion) {
        Component bookTitle = text("FloraCore Chat Page");
        Component bookAuthor = text("FloraCore");
        Collection<Component> bookPages = new ArrayList<>();
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        DataChatRecord dataChatRecord = reportDataChatRecord.getDataChatRecord();
        long startTime = dataChatRecord.getJoinTime();
        long endTime = dataChatRecord.getQuitTime();
        CHAT chat = getStorageImplementation().selectChatWithID(dataChatRecord.getId());
        List<ChatRecord> records = chat.getRecords();
        int startIndex = 0;
        int endIndex = records.size();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getTime() >= startTime) {
                startIndex = i;
                break;
            }
        }
        for (int i = startIndex; i < records.size(); i++) {
            if (records.get(i).getTime() > endTime) {
                endIndex = i;
                break;
            }
        }
        List<ChatRecord> filteredRecords = records.subList(startIndex, endIndex);
        Component main = join(joinConfig,
                TranslationManager.render(MenuMessage.COMMAND_MISC_CHAT.build(), uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_1.build(filteredRecords.size()), uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_START_TIME_BOOK.build(DurationFormatter.getTimeFromTimestamp(startTime)), uuid),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_END_TIME_BOOK.build(DurationFormatter.getTimeFromTimestamp(endTime)), uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_2.build(), uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_3.build(reportUUID, conclusion), uuid)
        ).asComponent();
        bookPages.add(main);
        for (ChatRecord record : filteredRecords) {
            Component c = join(joinConfig,
                    TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_CHAT.build(DurationFormatter.getTimeFromTimestamp(record.getTime()), getPlayerRecordName(record.getUuid()), record.getMessage(), record.getUuid().equals(reportDataChatRecord.getUuid())), uuid),
                    space(),
                    TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_3.build(reportUUID, conclusion), uuid));
            bookPages.add(c);
        }
        return Book.book(bookTitle, bookAuthor, bookPages);
    }
}
