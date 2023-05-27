package team.floracore.bukkit.commands.player;

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
import org.floracore.api.bukkit.messenger.message.type.*;
import org.floracore.api.commands.report.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.inevntory.*;
import team.floracore.bukkit.inevntory.content.*;
import team.floracore.bukkit.locale.chat.*;
import team.floracore.bukkit.locale.message.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.bukkit.util.itemstack.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static net.kyori.adventure.text.Component.*;

/**
 * Report命令
 */
@CommandPermission("floracore.command.report")
@CommandDescription("举报一名玩家")
public class ReportCommand extends FloraCoreBukkitCommand {
    private final List<REPORT> reports = new ArrayList<>();

    public ReportCommand(FCBukkitPlugin plugin) {
        super(plugin);
        reports.addAll(getStorageImplementation().getReports());
        plugin.getBootstrap().getScheduler().asyncRepeating(() -> {
            reports.clear();
            reports.addAll(getStorageImplementation().getReports());
        }, 3, TimeUnit.SECONDS);
    }

    @CommandMethod("report-tp <target>")
    @CommandPermission("floracore.command.report.staff")
    public void reportTeleport(final @NotNull Player sender, final @Argument("target") String target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        PlayerCommandMessage.COMMAND_REPORT_TP_TRANSMITTING.send(s);
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
                PlayerCommandMessage.COMMAND_REPORT_TP_SUCCESS.send(s, target);
            } else {
                MiscMessage.PLAYER_NOT_FOUND.send(s, target);
            }
        } else {
            getPlugin().getBukkitMessagingFactory().pushTeleport(u, ut, server);
            getPlugin().getBungeeUtil().connect(sender, server);
        }
    }

    @CommandMethod("report <target> <reason>")
    public void report(final @NotNull Player sender,
                       final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                       final @NotNull @Argument("reason") @Greedy String reason) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player t = Bukkit.getPlayer(target);
        UUID reportedUser;
        if (t != null) {
            if (t.getUniqueId() == sender.getUniqueId()) {
                PlayerCommandMessage.COMMAND_REPORT_SELF.send(s);
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
                PlayerCommandMessage.COMMAND_REPORT_NOT_PERMISSION.send(s);
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
            PlayerCommandMessage.COMMAND_REPORT_ABNORMAL.send(s);
            return;
        }
        REPORT report = getStorageImplementation().getUnprocessedReports(reportedUser);
        if (report != null) {
            if (report.getReporters().contains(s.getUniqueId())) {
                PlayerCommandMessage.COMMAND_REPORT_REPEAT.send(s);
                return;
            }
        }
        PlayerCommandMessage.COMMAND_REPORT_SUCCESS.send(s, target, reason);
        createReport(s.getUniqueId(), reportedUser, reporterServer, reportedUserServer, reason);
    }

    private void createReport(UUID reporter,
                              UUID reportedUser,
                              String reporterServer,
                              String reportedUserServer,
                              String reason) {
        getAsyncExecutor().execute(() -> {
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
            getPlugin().getBukkitMessagingFactory()
                    .pushReport(reporter, reportedUser, reporterServer, reportedUserServer, reason);
        });
    }

    private List<ReportDataChatRecord> getPlayerChatUUIDRecent(UUID reporter,
                                                               long time,
                                                               ChatAPI chatAPI,
                                                               ChatManager chatManager) {
        List<ReportDataChatRecord> c = chatAPI.getPlayerChatUUIDRecent(reporter, 3)
                .stream()
                .map(dataChatRecord -> new ReportDataChatRecord(reporter, ChatType.SERVER, dataChatRecord))
                .collect(Collectors.toList());
        ChatManager.MapPlayerRecord cm1 = chatManager.getMapPlayerRecord(reporter);
        if (cm1 != null) {
            int id = chatManager.getChat().getId();
            DataChatRecord d = new DataChatRecord(id, cm1.getJoinTime(), time);
            c.add(new ReportDataChatRecord(reporter, ChatType.SERVER, d));
        }
        List<ReportDataChatRecord> c1 = chatAPI.getPlayerChatRecentParty(reporter, 3)
                .stream()
                .map(dataChatRecord -> new ReportDataChatRecord(reporter, ChatType.PARTY, dataChatRecord))
                .collect(Collectors.toList());
        c.addAll(c1);
        return c;
    }

    @CommandMethod("reports")
    @CommandPermission("floracore.command.report.staff")
    public void reports(final @NotNull Player player) {
        Sender s = getPlugin().getSenderFactory().wrap(player);
        getReportsMainGui(player, false).open(player);
    }

    @CommandMethod("reports-chats|rcs <uuid> <conclusion>")
    @CommandPermission("floracore.command.report.staff")
    public void reportsChats(final @NotNull Player player,
                             final @NotNull @Argument("uuid") UUID uuid,
                             final @NotNull @Argument("conclusion") Boolean conclusion) {
        getChatsGUI(player, uuid, conclusion).open(player);
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
            title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_TITLE.build(), uuid)
                    .append(space())
                    .append(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_PROCESSED.build(), uuid));
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
                Component rt = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_TITLE.build(id),
                        uuid);
                List<Component> lore = getReportLore(report, uuid);
                lore.add(Component.space());
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid));
                ItemStackBuilder ri = new ItemStackBuilder(Material.PAPER).setName(rt).setLore(lore);
                if (report.getStatus() == ReportStatus.ACCEPTED) {
                    ri.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
                }
                items[i] = ClickableItem.of(ri.get(),
                        inventoryClickEvent -> getReportGui(player, report.getUniqueId(), conclusion).open(player));
            }
            pagination.setItems(items);
            pagination.setItemsPerPage(27);
            Component t = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_PAGE.build(pagination.getPage() + 1),
                    uuid);
            contents.set(0,
                    4,
                    ClickableItem.empty(new ItemStackBuilder(Material.BOOKSHELF).setName(title)
                            .setLore(Collections.singletonList(
                                    t))
                            .get()));
            int i = 18;
            for (ClickableItem pageItem : pagination.getPageItems()) {
                i++;
                contents.set(SmartInventory.getInventoryRow(i), SmartInventory.getInventoryColumn(i), pageItem);
            }
            supplementaryMenu(contents);
            if (conclusion) {
                Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
                contents.set(5,
                        5,
                        ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(back).get(),
                                event -> getReportsMainGui(player, false).open(player)));
            } else {
                Component t1 = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_PROCESSED.build(), uuid);
                contents.set(0,
                        8,
                        ClickableItem.of(new ItemStackBuilder(Material.CHEST).setName(t1).get(),
                                inventoryClickEvent -> getReportsMainGui(player, true).open(player)));
            }
            setPageSlot(player, conclusion, uuid, contents, pagination);
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5,
                    4,
                    ClickableItem.of(new ItemStackBuilder(Material.BARRIER).setName(close).get(),
                            event -> player.closeInventory()));
        });
        return builder.build();
    }

    private void setPageSlot(Player player,
                             boolean conclusion,
                             UUID uuid,
                             InventoryContents contents,
                             Pagination pagination) {
        if (!pagination.isFirst()) {
            Component previous = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(), uuid);
            Component turn = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage()),
                    uuid);
            contents.set(5,
                    0,
                    ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(previous)
                                    .setLore(Collections.singletonList(turn))
                                    .get(),
                            event -> getReportsMainGui(player, conclusion).open(player,
                                    pagination.previous().getPage())));
        }
        if (!pagination.isLast()) {
            Component next = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
            Component turn = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage() + 2),
                    uuid);
            contents.set(5,
                    8,
                    ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(next)
                                    .setLore(Collections.singletonList(turn))
                                    .get(),
                            event -> getReportsMainGui(player, conclusion).open(player, pagination.next().getPage())));
        }
    }

    private SmartInventory getReportGui(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space())
                .append(AbstractMessage.ARROW.color(NamedTextColor.GRAY))
                .append(Component.space())
                .append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(finalTitle);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            ItemStackBuilder i1 = new ItemStackBuilder(Material.PAPER).setName(finalTitle)
                    .setLore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
            contents.set(0, 4, ClickableItem.empty(i1.get()));
            String resultRns = getReports(report);
            ItemStack rs = getPlayerItemStackBuilder(report.getReporters().get(0)).setName(TranslationManager.render(
                            MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER.build(resultRns),
                            uuid))
                    .setLore(Collections.singletonList(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(),
                            uuid)))
                    .get();
            contents.set(2,
                    3,
                    ClickableItem.of(rs,
                            inventoryClickEvent -> getReportersGUI(player, reportUUID, conclusion).open(player)));
            String r1 = getPlayerRecordName(report.getReported());
            if (r1 == null) {
                r1 = "UNKNOWN";
            }
            boolean online = isOnline(report.getReported());
            ItemStackBuilder rds = getPlayerItemStackBuilder(report.getReported()).setName(TranslationManager.render(
                    MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTED.build(r1, online),
                    uuid));
            if (online) {
                rds.setLore(Collections.singletonList(TranslationManager.render(MiscMessage.CLICK_TP, uuid)));
                String finalR = r1;
                contents.set(2, 5, ClickableItem.of(rds.get(), inventoryClickEvent -> {
                    reportTeleport(player, finalR);
                    player.closeInventory();
                }));
            } else {
                contents.set(2, 5, ClickableItem.empty(rds.get()));
            }
            ItemStack chats = new ItemStackBuilder(Material.BOOKSHELF).setName(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_CHAT.build(),
                            uuid))
                    .setLore(Collections.singletonList(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(),
                            uuid)))
                    .get();
            contents.set(2,
                    4,
                    ClickableItem.of(chats,
                            inventoryClickEvent -> getChatsGUI(player, reportUUID, conclusion).open(player)));
            switch (report.getStatus()) {
                case WAITING:
                    Component accepted = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_ACCEPTED.build(),
                            uuid);
                    ItemStack ai = ItemStackBuilder.limeStainedGlassPane().setName(accepted).get();
                    contents.set(3, 4, ClickableItem.of(ai, inventoryClickEvent -> {
                        report.setStatus(ReportStatus.ACCEPTED);
                        getReportGui(player, reportUUID, conclusion).open(player);
                        String reported = getPlayerRecordName(report.getReported());
                        for (UUID reporter : report.getReporters()) {
                            getPlugin().getBukkitMessagingFactory().pushNoticeMessage(reporter,
                                    NoticeMessage.NoticeType.REPORT_ACCEPTED,
                                    Collections.singletonList(reported));
                        }
                        getPlugin().getBukkitMessagingFactory().pushNoticeMessage(UUID.randomUUID(),
                                NoticeMessage.NoticeType.REPORT_STAFF_ACCEPTED,
                                Arrays.asList(resultRns, reported));
                    }));
                    break;
                case ACCEPTED:
                    Component end = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_END.build(), uuid);
                    ItemStackBuilder ei = ItemStackBuilder.lightBlueStainedGlassPane().setName(end);
                    ei.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
                    contents.set(3, 4, ClickableItem.of(ei.get(), inventoryClickEvent -> {
                        report.setStatus(ReportStatus.ENDED);
                        report.setConclusionTime(System.currentTimeMillis());
                        getReportGui(player, reportUUID, conclusion).open(player);
                        String reported = getPlayerRecordName(report.getReported());
                        for (UUID reporter : report.getReporters()) {
                            getPlugin().getBukkitMessagingFactory().pushNoticeMessage(reporter,
                                    NoticeMessage.NoticeType.REPORT_PROCESSED,
                                    Collections.singletonList(reported));
                        }
                        getPlugin().getBukkitMessagingFactory().pushNoticeMessage(UUID.randomUUID(),
                                NoticeMessage.NoticeType.REPORT_STAFF_PROCESSED,
                                Arrays.asList(resultRns, reported));
                    }));
                    break;
                case ENDED:
                    Component ended = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_ENDED.build(),
                            uuid);
                    ItemStack edi = ItemStackBuilder.redStainedGlassPane().setName(ended).get();
                    contents.set(3, 4, ClickableItem.empty(edi));
                    break;
            }
            supplementaryMenu(contents);
            Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
            contents.set(5,
                    8,
                    ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(back).get(),
                            event -> getReportsMainGui(player, conclusion).open(player)));
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5,
                    4,
                    ClickableItem.of(new ItemStackBuilder(Material.BARRIER).setName(close).get(),
                            event -> player.closeInventory()));
        });
        return builder.build();
    }

    private SmartInventory getReportersGUI(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space())
                .append(AbstractMessage.ARROW.color(NamedTextColor.GRAY))
                .append(Component.space())
                .append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(finalTitle);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ItemStackBuilder i1 = new ItemStackBuilder(Material.PAPER).setName(finalTitle)
                    .setLore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
            contents.set(0, 4, ClickableItem.empty(i1.get()));
            List<UUID> reporters = report.getReporters();
            ClickableItem[] items = new ClickableItem[reporters.size()];
            for (int i = 0; i < items.length; i++) {
                String r1 = getPlayerRecordName(reporters.get(i));
                if (r1 == null) {
                    r1 = "UNKNOWN";
                }
                boolean online = isOnline(reporters.get(i));
                ItemStackBuilder rds = getPlayerItemStackBuilder(reporters.get(i)).setName(TranslationManager.render(
                        MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER_DETAILED.build(r1, online),
                        uuid));
                if (online) {
                    rds.setLore(Collections.singletonList(TranslationManager.render(MiscMessage.CLICK_TP, uuid)));
                    String finalR = r1;
                    items[i] = ClickableItem.of(rds.get(), inventoryClickEvent -> {
                        reportTeleport(player, finalR);
                        player.closeInventory();
                    });
                } else {
                    items[i] = ClickableItem.empty(rds.get());
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
            contents.set(5,
                    5,
                    ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(back).get(),
                            event -> getReportGui(player, reportUUID, conclusion).open(player)));
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5,
                    4,
                    ClickableItem.of(new ItemStackBuilder(Material.BARRIER).setName(close).get(),
                            event -> player.closeInventory()));
        });
        return builder.build();
    }

    private SmartInventory getChatsGUI(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        Audience target = getPlugin().getSenderFactory().getAudiences().player(player);
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_CHATS_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space())
                .append(AbstractMessage.ARROW.color(NamedTextColor.GRAY))
                .append(Component.space())
                .append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(finalTitle);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ItemStackBuilder i1 = new ItemStackBuilder(Material.PAPER).setName(finalTitle)
                    .setLore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
            contents.set(0, 4, ClickableItem.empty(i1.get()));
            List<ReportDataChatRecord> chats = report.getChat();
            ClickableItem[] items = new ClickableItem[chats.size()];
            for (int i = 0; i < items.length; i++) {
                ReportDataChatRecord chat = chats.get(i);
                ItemStackBuilder item = new ItemStackBuilder(Material.BOOK);
                int id = chat.getDataChatRecord().getId();
                Component ct = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_TITLE.build(
                        id), uuid);
                item.setName(ct);
                List<Component> lore = new ArrayList<>();
                lore.add(Component.space());
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_PLAYER.build(
                        getPlayerRecordName(chat.getUuid())), uuid));
                long startTime = chat.getDataChatRecord().getJoinTime();
                long endTime = chat.getDataChatRecord().getQuitTime();
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_START_TIME.build(
                        DurationFormatter.getTimeFromTimestamp(startTime)), uuid));
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_END_TIME.build(
                        DurationFormatter.getTimeFromTimestamp(endTime)), uuid));
                lore.add(Component.space());
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid));
                item.setLore(lore);
                items[i] = ClickableItem.of(item.get(),
                        inventoryClickEvent -> target.openBook(getChatPage(uuid, reportUUID, chat, conclusion)));
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
            contents.set(5,
                    5,
                    ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(back).get(),
                            event -> getReportGui(player, reportUUID, conclusion).open(player)));
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5,
                    4,
                    ClickableItem.of(new ItemStackBuilder(Material.BARRIER).setName(close).get(),
                            event -> player.closeInventory()));
        });
        return builder.build();
    }

    private ItemStackBuilder getPlayerItemStackBuilder(UUID uuid) {
        ItemStackBuilder ib = ItemStackBuilder.questionMark();
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
        return StringUtil.joinList(rns, 3);
    }

    private void supplementaryMenu(InventoryContents contents) {
        for (int j = 0; j < 9; j++) {
            ItemStack empty = ItemStackBuilder.grayStainedGlassPane().setName(Component.space()).get();
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
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(waiting),
                        uuid));
                break;
            case ACCEPTED:
                Component accepted = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_ACCEPTED.build(),
                        uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(accepted),
                        uuid));
                break;
            case ENDED:
                Component ended = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_ENDED.build(), uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(ended),
                        uuid));
                break;
        }
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_TIME.build(DurationFormatter.getTimeFromTimestamp(
                report.getReportTime())), uuid));
        lore.add(Component.space());
        String resultRns = getReports(report);
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER.build(resultRns), uuid));
        String reported = getPlayerRecordName(report.getReported());
        if (reported == null) {
            reported = "UNKNOWN";
        }
        boolean online = isOnline(report.getReported());
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTED.build(reported, online),
                uuid));
        String resultReason = StringUtil.joinList(report.getReasons(), 2);
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REASON.build(resultReason), uuid));
        return lore;
    }


    private Book getChatPage(UUID uuid,
                             UUID reportUUID,
                             ReportDataChatRecord reportDataChatRecord,
                             boolean conclusion) {
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
        ChatType chatType = reportDataChatRecord.getChatType();
        List<ChatRecord> filteredRecords = records.subList(startIndex, endIndex);
        Component main = join(joinConfig,
                TranslationManager.render(MenuMessage.COMMAND_MISC_CHAT.build()
                        .append(Component.space())
                        .append(MenuMessage.COMMAND_MISC_CHAT_TYPE.build(chatType)), uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_1.build(
                        filteredRecords.size()), uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_START_TIME_BOOK.build(
                        DurationFormatter.getTimeFromTimestamp(startTime)), uuid),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_END_TIME_BOOK.build(
                        DurationFormatter.getTimeFromTimestamp(endTime)), uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_2.build(),
                        uuid),
                space(),
                TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_3.build(
                        reportUUID,
                        conclusion), uuid)
        ).asComponent();
        bookPages.add(main);
        for (ChatRecord record : filteredRecords) {
            Component c = join(joinConfig,
                    TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_CHAT.build(
                            DurationFormatter.getTimeFromTimestamp(record.getTime()),
                            getPlayerRecordName(record.getUuid()),
                            record.getMessage(),
                            record.getUuid().equals(reportDataChatRecord.getUuid())), uuid),
                    space(),
                    TranslationManager.render(BookMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_CHATS_CHAT_BOOK_MAIN_LINE_3.build(
                            reportUUID,
                            conclusion), uuid));
            bookPages.add(c);
        }
        return Book.book(bookTitle, bookAuthor, bookPages);
    }
}
