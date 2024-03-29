package team.floracore.bungee.command.impl.socialsystems;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import org.floracore.api.FloraCoreProvider;
import org.floracore.api.bungee.messenger.message.type.NoticeMessage;
import org.floracore.api.data.DataType;
import org.floracore.api.data.chat.ChatType;
import org.floracore.api.socialsystems.party.PartySettings;
import org.floracore.api.socialsystems.party.Setting;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;
import team.floracore.bungee.locale.message.HelpMessage;
import team.floracore.bungee.locale.message.SocialSystemsMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;
import team.floracore.common.storage.misc.floracore.tables.PARTY;
import team.floracore.common.util.StringUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;

@CommandContainer
@CommandDescription("floracore.command.description.party")
@CommandPermission("floracore.socialsystems.party")
public class PartyCommand extends FloraCoreBungeeCommand implements Listener {
	public PartyCommand(FCBungeePlugin plugin) {
		super(plugin);
		plugin.getListenerManager().registerListener(this);
	}

	@CommandMethod("party|p <target>")
	@CommandDescription(EMPTY_DESCRIPTION)
	public void invite(final @NotNull ProxiedPlayer player,
	                   final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
		invite1(player, target);
	}

	@CommandMethod("party|p invite <target>")
	@CommandDescription("floracore.command.description.party.invite")
	public void invite1(final @NotNull ProxiedPlayer player,
	                    final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		UUID partyUUID;
		UUID leader;
		List<UUID> moderators = new ArrayList<>();
		List<UUID> members;
		if (data == null) {
			members = new ArrayList<>();
			// 获取队伍,如果不存在则创建队伍
			// 随机Party UUID
			partyUUID = UUID.randomUUID();
			// 获取队长的UUID
			leader = player.getUniqueId();
			members.add(leader);
			long createTime = System.currentTimeMillis();
			getAsyncExecutor().execute(() -> {
				getStorageImplementation().insertParty(partyUUID, leader, createTime);
				getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "party", partyUUID.toString(), 0);
				getStorageImplementation().insertData(uuid,
						DataType.SOCIAL_SYSTEMS_PARTY_HISTORY,
						String.valueOf(System.currentTimeMillis()),
						partyUUID.toString(),
						0);
			});
		} else {
			partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectEffectiveParty(partyUUID);
			leader = party.getLeader();
			moderators = party.getModerators();
			members = party.getMembers();
		}
		if (leader.equals(uuid) || moderators.contains(uuid)) {
			ProxiedPlayer tp = getPlugin().getProxy().getPlayer(target);
			UUID ut;
			if (tp == null) {
				ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
			} else {
				ut = tp.getUniqueId();
			}
			if (ut == null) {
				MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
				return;
			}
			if (!isOnline(ut)) {
				MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
				return;
			}
			if (ut.equals(uuid)) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_SELF.send(sender);
				return;
			}
			if (members.contains(ut)) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_ALREADY_IN_THE_TEAM.send(sender);
				return;
			}
			DATA tdp = getStorageImplementation().getSpecifiedData(ut, DataType.SOCIAL_SYSTEMS, "party");
			if (tdp != null) {
				UUID dpu = UUID.fromString(tdp.getValue());
				if (!dpu.equals(partyUUID)) {
					SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_ALREADY_IN_THE_OTHER_TEAM.send(sender);
					return;
				}
			}
			DATA td = getStorageImplementation().getSpecifiedData(ut,
					DataType.SOCIAL_SYSTEMS_PARTY_INVITE,
					partyUUID.toString());
			if (td != null || members.contains(ut)) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_HAS_BEEN_INVITED.send(sender);
				return;
			}
			Duration d = Duration.ofSeconds(65);
			// 将当前时间加上时间差
			Instant newTime = Instant.now().plus(d);
			// 将结果转换为时间戳
			long expiry = newTime.toEpochMilli();
			// 为了防止处理意外,设置65秒后自毁。
			getStorageImplementation().insertData(ut,
					DataType.SOCIAL_SYSTEMS_PARTY_INVITE,
					partyUUID.toString(),
					uuid.toString(),
					expiry);
			getPlugin().getBootstrap().getScheduler().asyncLater(() -> {
				// 获取目标玩家是否接受,如果未接受,则发送邀请玩家过期信息
				DATA inviteData = getStorageImplementation().getSpecifiedData(ut,
						DataType.SOCIAL_SYSTEMS_PARTY_INVITE,
						partyUUID.toString());
				if (inviteData != null) {
					// 邀请已过期
					getStorageImplementation().deleteDataID(inviteData.getId());
					getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(uuid,
									NoticeMessage.NoticeType.PARTY_INVITE_EXPIRED,
									Collections.singletonList(ut.toString()));
				}
			}, 1, TimeUnit.MINUTES);
			getAsyncExecutor().execute(() -> {
				getPlugin().getBungeeMessagingFactory()
						.pushNoticeMessage(ut,
								NoticeMessage.NoticeType.PARTY_ACCEPT,
								Arrays.asList(uuid.toString(), partyUUID.toString()));
				for (UUID member : members) {
					getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(member,
									NoticeMessage.NoticeType.PARTY_INVITE,
									Arrays.asList(uuid.toString(), ut.toString()));
				}
			});
		} else {
			SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_NO_PERMISSION.send(sender);
		}
	}

	@CommandMethod("party|p disband")
	@CommandDescription("floracore.command.description.party.disband")
	public void disband(final @NotNull ProxiedPlayer player) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			if (leader.equals(uuid)) {
				disband(partyUUID, uuid);
			} else {
				MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
			}
		}
	}

	public void disband(UUID partyUUID, UUID dissolved) {
		PARTY party = getStorageImplementation().selectParty(partyUUID);
		List<UUID> members = party.getMembers();
		getAsyncExecutor().execute(() -> {
			long disbandTime = System.currentTimeMillis();
			party.setDisbandTime(disbandTime);
			for (UUID member : members) {
				DATA md = getStorageImplementation().getSpecifiedData(member, DataType.SOCIAL_SYSTEMS, "party");
				if (md != null) {
					getStorageImplementation().deleteDataID(md.getId());
				}
				if (dissolved != null) {
					getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(member,
									NoticeMessage.NoticeType.PARTY_DISBAND,
									Collections.singletonList(dissolved.toString()));
				}
			}
		});
	}

	@CommandMethod("party|p remove <target>")
	@CommandDescription("floracore.command.description.party.remove")
	public void remove(final @NotNull ProxiedPlayer player, final @NotNull @Argument("target") String target) {
		kick(player, target);
	}

	@CommandMethod("party|p kick <target>")
	@CommandDescription(EMPTY_DESCRIPTION)
	public void kick(final @NotNull ProxiedPlayer player, final @NotNull @Argument("target") String target) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			List<UUID> moderators = party.getModerators();
			List<UUID> members = party.getMembers();
			ProxiedPlayer tp = getPlugin().getProxy().getPlayer(target);
			UUID ut;
			if (tp == null) {
				ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
			} else {
				ut = tp.getUniqueId();
			}
			if (leader.equals(uuid) || moderators.contains(uuid)) {
				if (ut == null) {
					MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
					return;
				}
				if (ut.equals(uuid)) {
					SocialSystemsMessage.COMMAND_MISC_PARTY_KICK_SELF.send(sender);
					return;
				}
				if (leader.equals(ut) || moderators.contains(ut)) {
					if (!leader.equals(uuid)) {
						SocialSystemsMessage.COMMAND_MISC_PARTY_KICK_NOT_PERMISSION.send(sender, ut);
						return;
					}
				}
				getAsyncExecutor().execute(() -> {
					DATA td = getStorageImplementation().getSpecifiedData(ut, DataType.SOCIAL_SYSTEMS, "party");
					getStorageImplementation().deleteDataID(td.getId());
					members.remove(ut);
					party.setMembers(members);
					getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(ut,
									NoticeMessage.NoticeType.PARTY_BE_KICKED,
									Collections.singletonList(uuid.toString()));
					members.forEach(member -> getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(member,
									NoticeMessage.NoticeType.PARTY_KICK,
									Collections.singletonList(ut.toString())));
				});
			} else {
				MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
			}
		}
	}

	@CommandMethod("party|p leave")
	@CommandDescription("floracore.command.description.party.leave")
	public void leave(final @NotNull ProxiedPlayer player) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			if (leader.equals(uuid)) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_LEAVE_IS_LEADER.send(sender);
				return;
			}
			List<UUID> moderators = party.getModerators();
			List<UUID> members = party.getMembers();
			members.remove(uuid);
			party.setMembers(members);
			if (moderators.contains(uuid)) {
				moderators.remove(uuid);
				party.setModerators(moderators);
			}
			getAsyncExecutor().execute(() -> {
				getStorageImplementation().deleteDataID(data.getId());
				getPlugin().getBungeeMessagingFactory()
						.pushNoticeMessage(uuid,
								NoticeMessage.NoticeType.PARTY_LEAVE,
								Collections.singletonList(uuid.toString()));
				members.forEach(member -> getPlugin().getBungeeMessagingFactory()
						.pushNoticeMessage(member,
								NoticeMessage.NoticeType.PARTY_LEAVE,
								Collections.singletonList(uuid.toString())));
			});

		}
	}

	@CommandMethod("party|p kickoffline")
	@CommandDescription("floracore.command.description.party.kickoffline")
	public void kickoffline(final @NotNull ProxiedPlayer player) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			List<UUID> moderators = party.getModerators();
			List<UUID> members = party.getMembers();
			if (leader.equals(uuid) || moderators.contains(uuid)) {
				getAsyncExecutor().execute(() -> {
					List<UUID> newMembers = members.stream()
							.filter(member -> isOnline(member) || member.equals(leader))
							.collect(Collectors.toList());

					List<UUID> offlineMembers = members.stream()
							.filter(member -> !isOnline(member) && !member.equals(leader))
							.collect(Collectors.toList());

					if (offlineMembers.isEmpty()) {
						SocialSystemsMessage.COMMAND_MISC_PARTY_KICKOFFLINE_NO_MEMBERS_AVAILABLE.send(sender);
						return;
					}

					party.setMembers(newMembers);

					offlineMembers.forEach(offlineMember -> {
						DATA od = getStorageImplementation().getSpecifiedData(offlineMember,
								DataType.SOCIAL_SYSTEMS,
								"party");
						getStorageImplementation().deleteDataID(od.getId());
						newMembers.forEach(member -> getPlugin().getBungeeMessagingFactory()
								.pushNoticeMessage(member,
										NoticeMessage.NoticeType.PARTY_KICK,
										Collections.singletonList(
												offlineMember.toString())));
					});
				});
			} else {
				MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
			}
		}
	}

	@CommandMethod("party|p accept <uuid>")
	@CommandDescription("floracore.command.description.party.accept")
	public void accept(final @NotNull ProxiedPlayer player, final @NotNull @Argument("uuid") String pu) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			try {
				UUID partyUUID = UUID.fromString(pu);
				DATA inviteData = getStorageImplementation().getSpecifiedData(uuid,
						DataType.SOCIAL_SYSTEMS_PARTY_INVITE,
						partyUUID.toString());
				if (inviteData == null) {
					SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_INVITED.send(sender);
					return;
				}
				PARTY party = getStorageImplementation().selectEffectiveParty(partyUUID);
				if (party == null) {
					SocialSystemsMessage.COMMAND_MISC_PARTY_INVALID.send(sender);
					return;
				}
				List<UUID> members = party.getMembers();
				if (members.contains(uuid)) {
					SocialSystemsMessage.COMMAND_MISC_PARTY_ALREADY_JOINED_THE_TEAM.send(sender);
					return;
				}
				members.add(uuid);
				party.setMembers(members);
				getStorageImplementation().deleteDataID(inviteData.getId());
				getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "party", partyUUID.toString(), 0);
				getStorageImplementation().insertData(uuid,
						DataType.SOCIAL_SYSTEMS_PARTY_HISTORY,
						String.valueOf(System.currentTimeMillis()),
						partyUUID.toString(),
						0);
				getAsyncExecutor().execute(() -> {
					for (UUID member : members) {
						getPlugin().getBungeeMessagingFactory()
								.pushNoticeMessage(member,
										NoticeMessage.NoticeType.PARTY_JOINED,
										Collections.singletonList(uuid.toString()));
					}
				});
			} catch (IllegalArgumentException e) {
				MiscMessage.COMMAND_MISC_INVALID_FORMAT.send(sender, pu);
			}
		} else {
			SocialSystemsMessage.COMMAND_MISC_PARTY_ALREADY_IN_THE_TEAM.send(sender);
		}
	}

	@CommandMethod("party|p list")
	@CommandDescription("floracore.command.description.party.list")
	public void list(final @NotNull ProxiedPlayer player) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			List<UUID> moderators = party.getModerators();
			List<UUID> members = party.getMembers();
			SocialSystemsMessage.COMMAND_MISC_PARTY_LIST.send(sender, leader, moderators, members);
		}
	}

	@CommandMethod("partychat|pc <message>")
	public void partyChat(final @NotNull ProxiedPlayer player,
	                      final @NotNull @Argument("message") @Greedy String message) {
		chat(player, message);
	}

	@CommandMethod("party|p chat <message>")
	@CommandDescription("floracore.command.description.party.chat")
	public void chat(final @NotNull ProxiedPlayer player, final @NotNull @Argument("message") @Greedy String message) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			List<UUID> members = party.getMembers();
			getAsyncExecutor().execute(() -> {
				for (UUID member : members) {
					getPlugin().getBungeeMessagingFactory()
							.pushChatMessage(member,
									ChatType.PARTY,
									Arrays.asList(uuid.toString(), message));
				}
				long time = System.currentTimeMillis();
				getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.PARTY,
						partyUUID.toString(),
						uuid,
						message,
						time));
			});
		}
	}

	@CommandMethod("party|p join <target>")
	@CommandDescription("floracore.command.description.party.join")
	public void join(final @NotNull ProxiedPlayer player,
	                 final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			ProxiedPlayer tp = getPlugin().getProxy().getPlayer(target);
			UUID ut;
			if (tp == null) {
				ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
			} else {
				ut = tp.getUniqueId();
			}
			if (ut == null) {
				MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
				return;
			}
			DATA td = getStorageImplementation().getSpecifiedData(ut, DataType.SOCIAL_SYSTEMS, "party");
			if (td == null) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_TARGET_NOT_IN.send(sender);
			} else {
				UUID partyUUID = UUID.fromString(td.getValue());
				PARTY party = getStorageImplementation().selectParty(partyUUID);
				if (party.getSettings().allJoin) {
					List<UUID> members = party.getMembers();
					members.add(uuid);
					party.setMembers(members);
					getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "party", partyUUID.toString(), 0);
					getStorageImplementation().insertData(uuid,
							DataType.SOCIAL_SYSTEMS_PARTY_HISTORY,
							String.valueOf(System.currentTimeMillis()),
							partyUUID.toString(),
							0);
					getAsyncExecutor().execute(() -> {
						for (UUID member : members) {
							getPlugin().getBungeeMessagingFactory()
									.pushNoticeMessage(member,
											NoticeMessage.NoticeType.PARTY_JOINED,
											Collections.singletonList(uuid.toString()));
						}
					});
				} else {
					SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_INVITED.send(sender);
				}
			}
		} else {
			SocialSystemsMessage.COMMAND_MISC_PARTY_ALREADY_IN_THE_TEAM.send(sender);
		}
	}


	@CommandMethod("party|p settings <setting> <value>")
	@CommandDescription("floracore.command.description.party.settings")
	public void settings(final @NotNull ProxiedPlayer player,
	                     final @NotNull @Argument("setting") Setting setting,
	                     final @NotNull @Argument("value") boolean value) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			List<UUID> moderators = party.getModerators();
			PartySettings partySettings = party.getSettings();
			if (leader.equals(uuid) || moderators.contains(uuid)) {
				if (setting == Setting.ALL_JOIN) {
					partySettings.allJoin = value;
					party.setSettings(partySettings);
					List<UUID> members = party.getMembers();
					getAsyncExecutor().execute(() -> {
						for (UUID member : members) {
							getPlugin().getBungeeMessagingFactory()
									.pushNoticeMessage(member,
											NoticeMessage.NoticeType.PARTY_SETTING_ALL_JOIN,
											Arrays.asList(uuid.toString(), String.valueOf(value)));
						}
					});
				}
			} else {
				MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
			}
		}
	}

	@CommandMethod("party|p warp")
	@CommandDescription("floracore.command.description.party.warp")
	public void warp(final @NotNull ProxiedPlayer player) {
		UUID uuid = player.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			List<UUID> moderators = party.getModerators();
			List<UUID> members = party.getMembers();
			if (members.size() == 1) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_NOT_ENOUGH_PEOPLE.send(sender);
				return;
			}
			if (leader.equals(uuid) || moderators.contains(uuid)) {
				String i = getPlayerListString(party.getUniqueId(), uuid);
				if (party.getMembers().size() - 1 > 3) {
					i = i + "...";
				}
				SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_SUCCESS.send(sender, i);
				getAsyncExecutor().execute(() -> {
					for (UUID member : members) {
						if (member.equals(uuid)) {
							continue;
						}
						if (leader.equals(uuid)) {
							getPlugin().getBungeeMessagingFactory()
									.pushNoticeMessage(member,
											NoticeMessage.NoticeType.PARTY_WARP_LEADER,
											Collections.singletonList(uuid.toString()));
						} else if (moderators.contains(uuid)) {
							getPlugin().getBungeeMessagingFactory()
									.pushNoticeMessage(member,
											NoticeMessage.NoticeType.PARTY_WARP_MODERATOR,
											Collections.singletonList(uuid.toString()));
						}
					}
					getPlugin().getBootstrap()
							.getScheduler()
							.asyncLater(() -> partyWarp(party.getUniqueId(),
											player.getServer().getInfo().getName()),
									1,
									TimeUnit.SECONDS);
				});
			} else {
				MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
			}
		}
	}

	public String getPlayerListString(UUID partyUUID, UUID self) {
		PARTY party = getStorageImplementation().selectParty(partyUUID);
		List<String> rns = new ArrayList<>();
		List<UUID> members = party.getMembers();
		for (UUID member : members) {
			String name = getPlayerRecordName(member);
			if (member.equals(self)) {
				continue;
			}
			if (name != null) {
				rns.add(name);
			}
		}
		return StringUtil.joinList(rns, 3);
	}

	public void partyWarp(UUID partyUUID, String serverName) {
		getAsyncExecutor().execute(() -> {
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			List<UUID> members = party.getMembers();
			for (UUID member : members) {
				ProxiedPlayer player = getPlugin().getProxy().getPlayer(member);
				if (player != null) {
					Server presentServer = player.getServer();
					if (presentServer == null) {
						getPlugin().getProxy().getScheduler().schedule(getPlugin().getLoader(), () -> player.connect(getPlugin().getProxy().getServerInfo(serverName)), 100L, TimeUnit.MILLISECONDS);
						return;
					}
					String serverNow = presentServer.getInfo().getName();
					if (!serverName.equalsIgnoreCase(serverNow)) {
						ServerInfo serverInfo = getPlugin().getProxy().getServerInfo(serverName);
						if (serverInfo != null) {
							player.connect(serverInfo);
						}
					}
				} else {
					throw new IllegalStateException("partyWarp():player is null, this should not happen!");
				}
			}
		});
	}

	@CommandMethod("party|p transfer <target>")
	@CommandDescription("floracore.command.description.party.transfer")
	public void transfer(@NotNull ProxiedPlayer s, @NotNull @Argument("target") String target) {
		UUID senderUUID = s.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(s);
		DATA data = getStorageImplementation().getSpecifiedData(senderUUID, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leaderUUID = party.getLeader();
			ProxiedPlayer tp = getPlugin().getProxy().getPlayer(target);
			UUID targetUUID;
			if (tp == null) {
				targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
			} else {
				targetUUID = tp.getUniqueId();
			}
			List<UUID> members = party.getMembers();
			if (members.size() == 1) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_NOT_ENOUGH_PEOPLE.send(sender);
				return;
			}
			if (!senderUUID.equals(leaderUUID)) { // 发送者不是队长,无权限转让
				SocialSystemsMessage.COMMAND_MISC_PARTY_TRANSFER_NO_PERMISSION.send(sender);
				return;
			}
			if (targetUUID == null) { // 目标玩家不存在
				MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
				return;
			}
			if (targetUUID.equals(senderUUID)) { // 准备转让给自己
				SocialSystemsMessage.COMMAND_MISC_PARTY_TRANSFER_SELF.send(sender);
				return;
			}
			if (!members.contains(targetUUID)) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_TARGET_NOT_IN.send(sender);
				return;
			}
			getAsyncExecutor().execute(() -> {
				List<UUID> moderators = party.getModerators();
				party.setLeader(targetUUID); // 更新目标为队长
				moderators.remove(targetUUID); // 目标不再是管理员
				moderators.add(senderUUID); // 将发送者设为管理员
				party.setModerators(moderators); // 更新管理员列表数据
				members.forEach(member -> getPlugin().getBungeeMessagingFactory()
						.pushNoticeMessage(member,
								NoticeMessage.NoticeType.PARTY_PROMOTE_LEADER,
								Arrays.asList(senderUUID.toString(),
										targetUUID.toString())));
			});
		}
	}

	@CommandMethod("party|p demote <target>")
	@CommandDescription("floracore.command.description.party.demote")
	public void demote(@NotNull ProxiedPlayer s, @NotNull @Argument("target") String target) {
		UUID senderUUID = s.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(s);
		DATA data = getStorageImplementation().getSpecifiedData(senderUUID, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leaderUUID = party.getLeader();
			ProxiedPlayer tp = getPlugin().getProxy().getPlayer(target);
			UUID targetUUID;
			if (tp == null) {
				targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
			} else {
				targetUUID = tp.getUniqueId();
			}
			List<UUID> members = party.getMembers();
			List<UUID> moderators = party.getModerators();
			if (members.size() == 1) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_NOT_ENOUGH_PEOPLE.send(sender);
				return;
			}
			if (!senderUUID.equals(leaderUUID)) { // 发送者不是队长,无权限转让
				SocialSystemsMessage.COMMAND_MISC_PARTY_TRANSFER_NO_PERMISSION.send(sender);
				return;
			}
			if (targetUUID == null) { // 目标玩家不存在
				MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
				return;
			}
			if (targetUUID.equals(senderUUID)) { // 准备转让给自己
				SocialSystemsMessage.COMMAND_MISC_PARTY_TRANSFER_SELF.send(sender);
				return;
			}
			if (!members.contains(targetUUID)) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_TARGET_NOT_IN.send(sender);
				return;
			}
			if (moderators.contains(targetUUID)) {
				getAsyncExecutor().execute(() -> {
					moderators.remove(targetUUID); // 目标不再是管理员
					party.setModerators(moderators); // 更新管理员列表数据
					members.forEach(member -> getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(member,
									NoticeMessage.NoticeType.PARTY_DEMOTE,
									Arrays.asList(senderUUID.toString(),
											targetUUID.toString())));
				});
			} else {
				SocialSystemsMessage.COMMAND_MISC_PARTY_DEMOTE_ALREADY_IN.send(sender, targetUUID);
			}
		}
	}

	@CommandMethod("party|p promote <target>")
	@CommandDescription("floracore.command.description.party.promote")
	public void promote(@NotNull ProxiedPlayer s, @NotNull @Argument("target") String target) {
		UUID senderUUID = s.getUniqueId();
		Sender sender = getPlugin().getSenderFactory().wrap(s);
		DATA data = getStorageImplementation().getSpecifiedData(senderUUID, DataType.SOCIAL_SYSTEMS, "party");
		if (data == null) {
			SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
		} else {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leaderUUID = party.getLeader();
			ProxiedPlayer tp = getPlugin().getProxy().getPlayer(target);
			UUID targetUUID;
			if (tp == null) {
				targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
			} else {
				targetUUID = tp.getUniqueId();
			}
			List<UUID> members = party.getMembers();
			List<UUID> moderators = party.getModerators();
			if (members.size() == 1) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_WARP_NOT_ENOUGH_PEOPLE.send(sender);
				return;
			}
			if (!senderUUID.equals(leaderUUID)) { // 发送者不是队长,无权限转让
				SocialSystemsMessage.COMMAND_MISC_PARTY_TRANSFER_NO_PERMISSION.send(sender);
				return;
			}
			if (targetUUID == null) { // 目标玩家不存在
				MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
				return;
			}
			if (targetUUID.equals(senderUUID)) { // 准备转让给自己
				SocialSystemsMessage.COMMAND_MISC_PARTY_TRANSFER_SELF.send(sender);
				return;
			}
			if (!members.contains(targetUUID)) {
				SocialSystemsMessage.COMMAND_MISC_PARTY_TARGET_NOT_IN.send(sender);
				return;
			}
			getAsyncExecutor().execute(() -> {
				if (moderators.contains(targetUUID)) {
					party.setLeader(targetUUID); // 更新目标为队长
					moderators.remove(targetUUID); // 目标不再是管理员
					moderators.add(senderUUID); // 将发送者设为管理员
					party.setModerators(moderators); // 更新管理员列表数据
					members.forEach(member -> getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(member,
									NoticeMessage.NoticeType.PARTY_PROMOTE_LEADER,
									Arrays.asList(senderUUID.toString(),
											targetUUID.toString())));
				} else {
					moderators.add(targetUUID);
					party.setModerators(moderators);
					members.forEach(member -> getPlugin().getBungeeMessagingFactory()
							.pushNoticeMessage(member,
									NoticeMessage.NoticeType.PARTY_PROMOTE_MODERATOR,
									Arrays.asList(senderUUID.toString(),
											targetUUID.toString())));
				}
			});
		}
	}

	@CommandMethod("party|p help")
	@CommandDescription("floracore.command.description.party.help")
	public void help(final @NotNull ProxiedPlayer player) {
		CommandHelpHandler.HelpTopic<CommandSender> partyCmds = getPlugin().getCommandManager().getManager().createCommandHelpHandler().queryHelp(player, "party");
		CommandHelpHandler.MultiHelpTopic<CommandSender> subCmds = (CommandHelpHandler.MultiHelpTopic<CommandSender>) partyCmds;
		JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
		Component helpComponent = HelpMessage.HELP_PARTY_TITLE.build();
		Sender sender = getPlugin().getSenderFactory().wrap(player);
		for (String childSuggestion : subCmds.getChildSuggestions()) {
			CommandHelpHandler.HelpTopic<CommandSender> subCmd = getPlugin().getCommandManager().getManager().createCommandHelpHandler().queryHelp(player, childSuggestion);
			CommandHelpHandler.VerboseHelpTopic<CommandSender> subCmdInfo = (CommandHelpHandler.VerboseHelpTopic<CommandSender>) subCmd;
			String description = subCmdInfo.getDescription();
			if (!description.equalsIgnoreCase(EMPTY_DESCRIPTION)) {
				helpComponent = Component.join(joinConfig, helpComponent, HelpMessage.HELP_PARTY_SUB_COMMAND.build(childSuggestion, description));
			}
		}
		helpComponent = Component.join(joinConfig, helpComponent, MiscMessage.PARTY_HORIZONTAL_LINE.build());
		sender.sendMessage(helpComponent);
	}

	@EventHandler
	public void onPlayerJoin(ServerConnectedEvent e) {
		ProxiedPlayer p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String serverName = e.getServer().getInfo().getName();
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data != null) {
			UUID partyUUID = UUID.fromString(data.getValue());
			PARTY party = getStorageImplementation().selectParty(partyUUID);
			UUID leader = party.getLeader();
			List<UUID> members = party.getMembers();
			if (members.size() == 1) {
				return;
			}
			if (!leader.equals(uuid)) {
				return;
			}
			switch (FloraCoreProvider.get().getServerAPI().getServerType(serverName)) {
				case GAME:
				case NORMAL:
					partyWarp(party.getUniqueId(), serverName);
					break;
			}
		}
	}

	private UUID findNewLeader(List<UUID> moderators, List<UUID> members) {
		for (UUID moderator : moderators) {
			if (isOnline(moderator)) {
				return moderator;
			}
		}
		for (UUID member : members) {
			if (isOnline(member)) {
				return member;
			}
		}
		return null;
	}

	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
		if (data != null) {
			UUID partyUUID = UUID.fromString(data.getValue());
			TaskScheduler scheduler = getPlugin().getProxy().getScheduler();
			AtomicBoolean shouldCancel = new AtomicBoolean(false);
			final int[] taskId = new int[1];
			taskId[0] = scheduler.schedule(getPlugin().getLoader(), new Runnable() {
				private int secondsElapsed = 1;
				private boolean offlineNotification = false;

				public void run() {
					boolean online = isOnline(uuid);
					PARTY party = getStorageImplementation().selectParty(partyUUID);
					UUID leader = party.getLeader();
					List<UUID> moderators = party.getModerators();
					List<UUID> members = party.getMembers();
					if (shouldCancel.get() || online) {
						// 取消任务
						if (online && offlineNotification) {
							members.forEach(member -> getPlugin().getBungeeMessagingFactory().pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_OFFLINE_RE_ONLINE, Collections.singletonList(uuid.toString())));
						}
						scheduler.cancel(taskId[0]);
						shouldCancel.set(true);
						return;
					}
					if (!offlineNotification) {
						members.forEach(member -> {
							if (leader.equals(uuid)) {
								getPlugin().getBungeeMessagingFactory().pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_OFFLINE_LEADER, Collections.singletonList(uuid.toString()));
							} else {
								getPlugin().getBungeeMessagingFactory().pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_OFFLINE, Collections.singletonList(uuid.toString()));
							}
						});
						offlineNotification = true;
					}
					// 如果超过5分钟未上线
					if (secondsElapsed >= 300) {
						moderators.remove(uuid);
						party.setModerators(moderators);
						members.remove(uuid);
						party.setMembers(members);
						shouldCancel.set(true);
						DATA od = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
						getStorageImplementation().deleteDataID(od.getId());
						// 执行队伍队长转移。
						if (leader.equals(uuid)) {
							UUID newLeader = findNewLeader(moderators, members);
							if (newLeader == null) {
								// 解散队伍
								disband(partyUUID, null);
								return;
							}
							party.setLeader(newLeader);
							members.forEach(member -> getPlugin().getBungeeMessagingFactory().pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_OFFLINE_TRANSFER, Arrays.asList(uuid.toString(), newLeader.toString())));
						} else {
							members.forEach(member -> getPlugin().getBungeeMessagingFactory().pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_OFFLINE_KICK, Collections.singletonList(uuid.toString())));
						}
					}
					secondsElapsed++;
				}
			}, 1, 1, TimeUnit.SECONDS).getId();
		}
	}
}
