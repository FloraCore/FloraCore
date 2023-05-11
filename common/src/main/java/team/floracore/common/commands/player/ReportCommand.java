package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import de.myzelyam.api.vanish.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.floracore.api.commands.report.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.locale.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.stream.*;

import static team.floracore.common.util.ReflectionWrapper.*;

/**
 * Report命令
 */
@CommandPermission("floracore.command.report")
@CommandDescription("举报一名玩家")
public class ReportCommand extends AbstractFloraCoreCommand {
    public static final boolean ADVANCED_VERSION = isVersionGreaterThanOrEqual(getVersion(), "v1_13_R1");

    public ReportCommand(FloraCorePlugin plugin) {
        super(plugin);
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
        // getReportsMainGui(player).open(player);
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

    /*private PaginatedGui getReportsMainGui(Player player) {
        UUID uuid = player.getUniqueId();
        Component title = TranslationManager.render(Message.COMMAND_REPORTS_MAIN_TITLE.build(0), uuid);
        PaginatedGui gui = Gui.paginated()
                .title(title)
                .rows(6)
                .pageSize(27)
                .create();
        gui.disableAllInteractions();
        title = TranslationManager.render(Message.COMMAND_REPORTS_MAIN_TITLE.build(gui.getCurrentPageNum()), uuid);
        gui.updateTitle(title);

        for (int i = 0; i < 9; i++) {
            GuiItem empty;
            if (ADVANCED_VERSION) {
                empty = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.space()).asGuiItem();
            } else {
                empty = ItemBuilder.from(new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 7)).name(Component.space()).asGuiItem();
            }
            gui.setItem(6, i + 1, empty);
            gui.setItem(2, i + 1, empty);
        }

        if (gui.getPagesNum() > 0) {
            GuiItem previous = ItemBuilder.from(Material.PAPER).setName("Previous").asGuiItem(event -> gui.previous());
            gui.setItem(6, 4, previous);
        }

        if (gui.getCurrentPageNum() + 1 <= gui.getPagesNum()) {
            GuiItem next = ItemBuilder.from(Material.PAPER).setName("Next").asGuiItem(event -> gui.next());
            gui.setItem(6, 6, next);
        }

        GuiItem close = ItemBuilder.from(Material.BARRIER).setName("Close").asGuiItem(event -> gui.close(player));
        gui.setItem(6, 5, close);

        for (int i = 0; i < 100; i++) {
            GuiItem e = ItemBuilder.from(Material.STONE).setName(String.valueOf(i)).asGuiItem();
            gui.addItem(e);
        }

        return gui;
    }*/
}
