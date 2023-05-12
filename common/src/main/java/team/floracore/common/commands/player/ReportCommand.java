package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import de.myzelyam.api.vanish.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.floracore.api.commands.report.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.inevntory.*;
import team.floracore.common.inevntory.content.*;
import team.floracore.common.locale.*;
import team.floracore.common.locale.data.chat.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static team.floracore.common.util.ReflectionWrapper.*;

/**
 * Report命令
 */
@CommandPermission("floracore.command.report")
@CommandDescription("举报一名玩家")
public class ReportCommand extends AbstractFloraCoreCommand {
    public static final boolean ADVANCED_VERSION = isVersionGreaterThanOrEqual(getVersion(), "v1_13_R1");
    private final List<Report> reports = new ArrayList<>();

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
            Message.PLAYER_NOT_FOUND.send(s, target);
            return;
        }
        String server;
        Data data = getStorageImplementation().getSpecifiedData(ut, DataType.FUNCTION, "server-status");
        if (data != null) {
            server = data.getValue();
        } else {
            Message.PLAYER_NOT_FOUND.send(s, target);
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
                Message.PLAYER_NOT_FOUND.send(s, target);
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
                Message.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
            if (hasPermission(ut, "floracore.command.report.bypass")) {
                Message.COMMAND_REPORT_NOT_PERMISSION.send(s);
                return;
            }
            reportedUser = ut;
        }
        final String reporterServer = getPlugin().getServerName();
        Data data = getStorageImplementation().getSpecifiedData(reportedUser, DataType.FUNCTION, "server-status");
        final String reportedUserServer;
        if (data != null) {
            reportedUserServer = data.getValue();
        } else {
            Message.COMMAND_REPORT_ABNORMAL.send(s);
            return;
        }
        Report report = getStorageImplementation().getUnprocessedReports(reportedUser);
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
        getReportsMainGui(player).open(player);
    }

    private void createReport(UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer, String reason) {
        getPlugin().getMessagingService().ifPresent(service -> {
            UUID uuid = UUID.randomUUID();
            long time = System.currentTimeMillis();
            List<ReportDataChatRecord> chat = new ArrayList<>();
            ChatAPI chatAPI = getPlugin().getApiProvider().getChatAPI();
            ChatManager chatManager = getPlugin().getChatManager();
            List<ReportDataChatRecord> c1 = chatAPI.getPlayerChatUUIDRecent(reporter, 3)
                    .stream()
                    .map(dataChatRecord -> new ReportDataChatRecord(reporter, dataChatRecord))
                    .collect(Collectors.toList());
            ChatManager.MapPlayerRecord cm1 = chatManager.getMapPlayerRecord(reporter);
            if (cm1 != null) {
                int id = chatManager.getChat().getId();
                DataChatRecord d = new DataChatRecord(id, cm1.getJoinTime(), time);
                c1.add(new ReportDataChatRecord(reporter, d));
            }
            List<ReportDataChatRecord> c2 = chatAPI.getPlayerChatUUIDRecent(reportedUser, 3)
                    .stream()
                    .map(dataChatRecord -> new ReportDataChatRecord(reportedUser, dataChatRecord))
                    .collect(Collectors.toList());
            ChatManager.MapPlayerRecord cm2 = chatManager.getMapPlayerRecord(reportedUser);
            if (cm2 != null) {
                int id = chatManager.getChat().getId();
                DataChatRecord d = new DataChatRecord(id, cm2.getJoinTime(), time);
                c2.add(new ReportDataChatRecord(reportedUser, d));
            }
            chat.addAll(c1);
            chat.addAll(c2);
            getStorageImplementation().addReport(uuid, reporter, reportedUser, reason, time, chat);
            service.pushReport(reporter, reportedUser, reporterServer, reportedUserServer, reason);
        });
    }

    private SmartInventory getReportsMainGui(Player player) {
        UUID uuid = player.getUniqueId();
        Component title = TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_TITLE.build(), uuid);
        List<Report> filteredReports = reports.stream()
                .filter(report -> report.getHandler() == null || report.getHandler().equals(uuid))
                .filter(report -> report.getHandler() == null || !Boolean.FALSE.equals(report.getConclusion()))
                .sorted(Comparator.comparingInt(Report::getId))
                .collect(Collectors.toList());
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(title);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ClickableItem[] items = new ClickableItem[filteredReports.size()];
            for (int i = 0; i < items.length; i++) {
                Report report = filteredReports.get(i);
                int id = report.getId();
                Component rt = TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REPORT_TITLE.build(id), uuid);
                List<Component> lore = new ArrayList<>();
                lore.add(Component.space());
                if (report.getHandler() == null) {
                    Component waiting = TranslationManager.render(Message.COMMAND_REPORTS_STATUS_WAITING.build(), uuid);
                    lore.add(TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(waiting), uuid));
                } else {
                    if (report.getConclusion()) {
                        Component ended = TranslationManager.render(Message.COMMAND_REPORTS_STATUS_ENDED.build(), uuid);
                        lore.add(TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(ended), uuid));
                    } else {
                        Component accepted = TranslationManager.render(Message.COMMAND_REPORTS_STATUS_ACCEPTED.build(), uuid);
                        lore.add(TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(accepted), uuid));
                    }
                }
                lore.add(TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REPORT_TIME.build(DurationFormatter.getTimeFromTimestamp(report.getReportTime())), uuid));
                lore.add(Component.space());
                List<String> rns = new ArrayList<>();
                for (UUID reporter : report.getReporters()) {
                    String name = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordName(reporter);
                    if (name != null) {
                        rns.add(name);
                    }
                }
                String resultRns = joinList(rns, 3);
                lore.add(TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REPORTER.build(resultRns), uuid));
                String reported = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordName(report.getReported());
                if (reported == null) {
                    reported = "UNKNOWN";
                }
                boolean online = getPlugin().getApiProvider().getPlayerAPI().isOnline(report.getReported());
                lore.add(TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REPORTED.build(reported, online), uuid));
                String resultReason = joinList(report.getReasons(), 2);
                lore.add(TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_REASON.build(resultReason), uuid));
                lore.add(Component.space());
                lore.add(TranslationManager.render(Message.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid));
                ItemBuilder ri = new ItemBuilder(Material.PAPER).displayName(rt).lore(lore);
                items[i] = ClickableItem.empty(ri.build());
            }
            pagination.setItems(items);
            pagination.setItemsPerPage(21);
            Component t = TranslationManager.render(Message.COMMAND_REPORTS_GUI_PAGE.build(pagination.getPage() + 1), uuid);
            contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.BOOKSHELF).displayName(title).lore(t).build()));
            int i = 19;
            for (ClickableItem pageItem : pagination.getPageItems()) {
                i++;
                if (i % 9 == 0) {
                    i = i + 2;
                }
                contents.set(SmartInventory.getInventoryRow(i), SmartInventory.getInventoryColumn(i), pageItem);
            }
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
            if (!pagination.isFirst()) {
                Component previous = TranslationManager.render(Message.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(), uuid);
                Component turn = TranslationManager.render(Message.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage()), uuid);
                contents.set(5, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(previous).lore(turn).build(), event -> getReportsMainGui(player).open(player, pagination.previous().getPage())));
            }
            if (!pagination.isLast()) {
                Component next = TranslationManager.render(Message.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
                Component turn = TranslationManager.render(Message.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage() + 2), uuid);
                contents.set(5, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(next).lore(turn).build(), event -> getReportsMainGui(player).open(player, pagination.next().getPage())));
            }
            Component close = TranslationManager.render(Message.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).displayName(close).build(), event -> player.closeInventory()));
        });
        return builder.build();
    }
}
