package team.floracore.bukkit.messaging;

import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.floracore.api.bukkit.messenger.message.type.NoticeMessage;
import org.floracore.api.bukkit.messenger.message.type.TeleportMessage;
import org.floracore.api.messenger.message.Message;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.messaging.message.NoticeMessageImpl;
import team.floracore.bukkit.messaging.message.TeleportMessageImpl;
import team.floracore.common.messaging.InternalMessagingService;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.common.messaging.message.BungeeCommandImpl;
import team.floracore.common.messaging.message.KickMessageImpl;
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
		this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> getPlugin().getMessagingService().ifPresent(service -> {
			UUID requestId = service.generatePingId();
			this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
			NoticeMessageImpl noticeMessage = new NoticeMessageImpl(requestId, receiver, type, parameters);
			service.getMessenger().sendOutgoingMessage(noticeMessage);
			notice(noticeMessage);
		}));
	}

	public void notice(NoticeMessage noticeMsg) {
		Player player = Bukkit.getPlayer(noticeMsg.getReceiver());
		List<String> parameters = noticeMsg.getParameters();
	}

	private String getPlayerName(UUID uuid) {
		return getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
	}

	private boolean isPlayerOnline(UUID uuid) {
		return getPlugin().getApiProvider().getPlayerAPI().isOnline(uuid);
	}

	public void pushTeleport(UUID sender, UUID recipient, String serverName) {
		this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> getPlugin().getMessagingService().ifPresent(service -> {
			UUID requestId = service.generatePingId();
			this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
			TeleportMessageImpl teleportMessage = new TeleportMessageImpl(requestId, sender, recipient,
					serverName);
			service.getMessenger().sendOutgoingMessage(teleportMessage);
		}));
	}

	public void submitKick(UUID recipient, String reason) {
		this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> getPlugin().getMessagingService().ifPresent(service -> {
			UUID requestId = service.generatePingId();
			this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
			KickMessageImpl kickMessage = new KickMessageImpl(requestId, recipient, reason);
			service.getMessenger().sendOutgoingMessage(kickMessage);
		}));
	}

	public void bungeeCommand(String command) {
		this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> getPlugin().getMessagingService().ifPresent(service -> {
			UUID requestId = service.generatePingId();
			this.getPlugin().getLogger().info("[Messaging] Sending ping with id: " + requestId);
			BungeeCommandImpl bungeeCommand = new BungeeCommandImpl(requestId, command);
			service.getMessenger().sendOutgoingMessage(bungeeCommand);
		}));
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
								Bukkit.getScheduler().runTask(getPlugin().getLoader(), () -> {
									sender.teleport(recipient.getLocation());
								});
							}, 300, TimeUnit.MILLISECONDS);
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
