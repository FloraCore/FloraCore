package team.floracore.bukkit.messaging;

import com.google.gson.JsonElement;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.floracore.api.bukkit.messenger.message.type.NoticeMessage;
import org.floracore.api.bukkit.messenger.message.type.TeleportMessage;
import org.floracore.api.messenger.message.Message;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.bukkit.messaging.message.NoticeMessageImpl;
import team.floracore.bukkit.messaging.message.ReportMessageImpl;
import team.floracore.bukkit.messaging.message.TeleportMessageImpl;
import team.floracore.common.messaging.InternalMessagingService;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.common.sender.Sender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BukkitMessagingFactory extends MessagingFactory<FCBukkitPlugin> {
    public BukkitMessagingFactory(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected InternalMessagingService getServiceFor(String messagingType) {
        return super.getServiceFor(messagingType);
    }

    public void pushNoticeMessage(UUID receiver, NoticeMessage.NoticeType type, List<String> parameters) {
        this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                UUID requestId = service.generatePingId();
                this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
                NoticeMessageImpl noticeMessage = new NoticeMessageImpl(requestId, receiver, type, parameters);
                service.getMessenger().sendOutgoingMessage(noticeMessage);
                notice(noticeMessage);
            });
        });
    }

    public void notice(NoticeMessage noticeMsg) {
        Player player = Bukkit.getPlayer(noticeMsg.getReceiver());
        List<String> parameters = noticeMsg.getParameters();
        switch (noticeMsg.getType()) {
            case REPORT_STAFF_ACCEPTED:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.report.staff")) {
                        PlayerCommandMessage.COMMAND_MISC_REPORT_NOTICE_STAFF_ACCEPTED.send(i,
                                parameters.get(0),
                                parameters.get(1));
                    }
                });
                break;
            case REPORT_STAFF_PROCESSED:
                getPlugin().getOnlineSenders().forEach(i -> {
                    if (i.hasPermission("floracore.report.staff")) {
                        PlayerCommandMessage.COMMAND_MISC_REPORT_NOTICE_STAFF_PROCESSED.send(i,
                                parameters.get(0),
                                parameters.get(1));
                    }
                });
                break;
        }
        if (player != null) {
            Sender sender = getPlugin().getSenderFactory().wrap(player);
            switch (noticeMsg.getType()) {
                case REPORT_ACCEPTED:
                    PlayerCommandMessage.COMMAND_MISC_REPORT_NOTICE_ACCEPTED.send(sender, parameters.get(0));
                    PlayerCommandMessage.COMMAND_MISC_REPORT_THANKS.send(sender);
                    break;
                case REPORT_PROCESSED:
                    PlayerCommandMessage.COMMAND_MISC_REPORT_NOTICE_PROCESSED.send(sender, parameters.get(0));
                    PlayerCommandMessage.COMMAND_MISC_REPORT_THANKS.send(sender);
                    break;
            }
        }
    }

    public void pushReport(UUID reporter,
                           UUID reportedUser,
                           String reporterServer,
                           String reportedUserServer,
                           String reason) {
        this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                UUID requestId = service.generatePingId();
                this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
                ReportMessageImpl reportMessage = new ReportMessageImpl(requestId,
                        reporter,
                        reportedUser,
                        reporterServer,
                        reportedUserServer,
                        reason);
                service.getMessenger().sendOutgoingMessage(reportMessage);
                String player = getPlayerName(reporter);
                String target = getPlayerName(reportedUser);
                boolean playerOnlineStatus = isPlayerOnline(reporter);
                boolean targetOnlineStatus = isPlayerOnline(reportedUser);
                notifyStaffReport(player,
                        target,
                        reporterServer,
                        reportedUserServer,
                        reason,
                        playerOnlineStatus,
                        targetOnlineStatus);

            });
        });
    }

    private String getPlayerName(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
    }

    private boolean isPlayerOnline(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().isOnline(uuid);
    }

    public void notifyStaffReport(String reporter,
                                  String reportedUser,
                                  String reporterServer,
                                  String reportedUserServer,
                                  String reason,
                                  boolean playerOnlineStatus,
                                  boolean targetOnlineStatus) {
        getPlugin().getOnlineSenders().forEach(i -> {
            if (i.hasPermission("floracore.report.staff")) {
                PlayerCommandMessage.COMMAND_MISC_REPORT_BROADCAST.send(i,
                        reporter,
                        reportedUser,
                        reporterServer,
                        reportedUserServer,
                        reason,
                        playerOnlineStatus,
                        targetOnlineStatus);
            }
        });
    }

    public void pushTeleport(UUID sender, UUID recipient, String serverName) {
        this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                UUID requestId = service.generatePingId();
                this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
                TeleportMessageImpl teleportMessage = new TeleportMessageImpl(requestId, sender, recipient, serverName);
                service.getMessenger().sendOutgoingMessage(teleportMessage);
            });
        });
    }

    public boolean processIncomingMessage(String type, JsonElement content, UUID id) {
        // decode message
        Message decoded;
        switch (type) {
            case NoticeMessageImpl.TYPE:
                decoded = NoticeMessageImpl.decode(content, id);
                break;
            case TeleportMessageImpl.TYPE:
                decoded = TeleportMessageImpl.decode(content, id);
                break;
            default:
                return false;

        }
        // consume the message
        processIncomingMessage(decoded);
        return true;
    }

    private void processIncomingMessage(Message message) {
        if (message instanceof NoticeMessage) {
            NoticeMessage noticeMsg = (NoticeMessage) message;
            notice(noticeMsg);
        } else if (message instanceof TeleportMessage) {
            TeleportMessage teleportMsg = (TeleportMessage) message;
            UUID su = teleportMsg.getSender();
            UUID ru = teleportMsg.getRecipient();
            String serverName = teleportMsg.getServerName();
            if (serverName.equalsIgnoreCase(getPlugin().getServerName())) {
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                AtomicBoolean shouldCancel = new AtomicBoolean(false);
                final int[] taskId = new int[1];
                taskId[0] = scheduler.runTaskTimerAsynchronously(getPlugin().getLoader(), new Runnable() {
                    private int secondsElapsed = 0;

                    public void run() {
                        Player sender = Bukkit.getPlayer(su);
                        Player recipient = Bukkit.getPlayer(ru);
                        if (shouldCancel.get() || secondsElapsed >= 30 || recipient == null) {
                            // 取消任务
                            scheduler.cancelTask(taskId[0]);
                            return;
                        }
                        if (sender != null) {
                            Sender s = getPlugin().getSenderFactory().wrap(sender);
                            getPlugin().getBootstrap().getScheduler().asyncLater(() -> {
                                if (getPlugin().getLoader()
                                        .getServer()
                                        .getPluginManager()
                                        .getPlugin("PremiumVanish") != null) {
                                    if (!VanishAPI.isInvisible(sender)) {
                                        VanishAPI.hidePlayer(sender);
                                    }
                                }
                            }, 300, TimeUnit.MILLISECONDS);
                            sender.teleport(recipient.getLocation());
                            PlayerCommandMessage.COMMAND_REPORT_TP_SUCCESS.send(s, recipient.getDisplayName());
                            shouldCancel.set(true);
                        }
                        secondsElapsed++;
                    }
                }, 0L, 20L).getTaskId();
            }
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
        }
    }
}
