package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.floracore.api.FloraCoreProvider;
import org.floracore.api.bungee.messenger.message.type.NoticeMessage;
import org.floracore.api.data.DataType;
import org.floracore.api.data.chat.ChatType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;
import team.floracore.bungee.locale.message.SocialSystemsMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;
import team.floracore.common.storage.misc.floracore.tables.PARTY;
import team.floracore.common.util.StringUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CommandContainer
@CommandDescription("floracore.command.description.party")
@CommandPermission("floracore.socialsystems.party")
public class PartyCommand extends FloraCoreBungeeCommand implements Listener {
	private final CommandManager commandManager;

	public PartyCommand(FCBungeePlugin plugin, CommandManager commandManager) {
		super(plugin);
		this.commandManager = commandManager;
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
			UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
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
			UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
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
						SocialSystemsMessage.COMMAND_MISC_PARTY_KICK_NOT_PERMISSION.send(sender, target);
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
				members.forEach(member -> {
					getPlugin().getBungeeMessagingFactory()
					           .pushNoticeMessage(member,
							           NoticeMessage.NoticeType.PARTY_LEAVE,
							           Collections.singletonList(uuid.toString()));
				});
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
					String serverNow = player.getServer().getInfo().getName();
					if (!serverName.equalsIgnoreCase(serverNow)) {
						ServerInfo serverInfo = getPlugin().getProxy().getServerInfo(serverName);
						if (serverInfo != null) {
							player.connect(serverInfo);
						}
					}
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
			UUID targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
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
			UUID targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
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
				SocialSystemsMessage.COMMAND_MISC_PARTY_DEMOTE_ALREADY_IN.send(sender, target);
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
			UUID targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
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
}
