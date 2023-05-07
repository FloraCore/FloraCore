package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import de.myzelyam.api.vanish.*;
import net.luckperms.api.*;
import net.luckperms.api.model.user.*;
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

    @CommandMethod("report-tp <target> <server>")
    @CommandPermission("floracore.command.report.staff")
    public void reportTP(final @NotNull Player sender, final @Argument("target") String target, final @Argument("server") String server) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Message.COMMAND_REPORT_TP_TRANSMITTING.send(s);
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
            // TODO 跨服传送

            getPlugin().getBungeeUtil().connect(sender, server);
        }
    }

    @CommandMethod("report <target> <reason>")
    public void report(final @NotNull Player s, final @NotNull String target, final @NotNull String reason) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        Player t = Bukkit.getPlayer(target);
        UUID reportedUser;
        if (t != null) {
            reportedUser = t.getUniqueId();
        } else {
            User user = luckPerms.getUserManager().getUser(target);
            if (user == null) {
                // TODO 这名玩家从未上线过服务器
                return;
            }
            if (user.getCachedData().getPermissionData().checkPermission("floracore.command.report.bypass").asBoolean()) {
                // TODO 你无权举报这名玩家
                return;
            }
            reportedUser = user.getUniqueId();
        }
        final String reporterServer = getPlugin().getServerName();
        Data data = getStorageImplementation().getSpecifiedData(reportedUser, DataType.FUNCTION, "server-status");
        final String reportedUserServer;
        if (data != null) {
            reportedUserServer = data.getValue();
        } else {
            // TODO 这名玩家的数据异常!
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
