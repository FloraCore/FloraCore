package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import de.myzelyam.api.vanish.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.floracore.api.commands.report.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.gui.guis.*;
import team.floracore.common.locale.*;
import team.floracore.common.locale.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.stream.*;

/**
 * Report命令
 */
@CommandPermission("floracore.command.report")
@CommandDescription("举报一名玩家")
public class ReportCommand extends AbstractFloraCoreCommand {
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
    public void reports(final @NotNull Player sender) {
        Gui gui = Gui.gui()
                .title(Component.text("GUI Title!"))
                .rows(6)
                .create();
        gui.open(sender);
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
}
