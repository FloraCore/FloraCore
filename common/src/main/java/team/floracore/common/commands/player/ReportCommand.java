package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import de.myzelyam.api.vanish.*;
import net.luckperms.api.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

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
        String server;
        Data data = getStorageImplementation().getSpecifiedData(u, DataType.FUNCTION, "server-status");
        if (data != null) {
            server = data.getValue();
        } else {
            Message.PLAYER_NOT_FOUND.send(s, target);
            return;
        }
        if (getPlugin().getServerName().equalsIgnoreCase(server)) {
            Player t = Bukkit.getPlayer(target);
            if (t != null) {
                if (!VanishAPI.isInvisible(sender)) {
                    VanishAPI.hidePlayer(sender);
                }
                sender.teleport(t.getLocation());
                Message.COMMAND_REPORT_TP_SUCCESS.send(s, target);
            } else {
                Message.PLAYER_NOT_FOUND.send(s, target);
            }
        } else {
            UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
            if (ut == null) {
                Message.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
            getPlugin().getMessagingService().ifPresent(service -> service.pushTeleport(u, ut, server));
            getPlugin().getBungeeUtil().connect(sender, server);
        }
    }

    @CommandMethod("report <target> <reason>")
    public void report(final @NotNull Player sender, final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target, final @NotNull @Argument("reason") @Greedy String reason) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        LuckPerms luckPerms = LuckPermsProvider.get();
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
        createReport(s.getUniqueId(), reportedUser, reporterServer, reportedUserServer, reason);
    }

    private void createReport(UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer, String reason) {
        getPlugin().getMessagingService().ifPresent(service -> {
            // TODO 写入数据库
            service.pushReport(reporter, reportedUser, reporterServer, reportedUserServer, reason);
        });
    }
}
