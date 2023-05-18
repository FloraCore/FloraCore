package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.specifier.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.floracore.api.messenger.message.type.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

@CommandContainer
@CommandDescription("组队是一个社交系统。玩家可以与其他玩家一起游玩")
@CommandPermission("floracore.socialsystems.party")
public class PartyCommand extends AbstractFloraCoreCommand implements Listener {
    public PartyCommand(FloraCorePlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    @CommandMethod("party|p <target>")
    public void invite(final @NonNull Player player, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
        invite1(player, target);
    }

    @CommandMethod("party|p invite <target>")
    public void invite1(final @NonNull Player player, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
        UUID uuid = player.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(player);
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
        UUID partyUUID;
        UUID leader;
        List<UUID> moderators = new ArrayList<>();
        List<UUID> members;
        if (data == null) {
            members = new ArrayList<>();
            // 获取队伍，如果不存在则创建队伍
            // 随机Party UUID
            partyUUID = UUID.randomUUID();
            // 获取队长的UUID
            leader = player.getUniqueId();
            members.add(leader);
            long createTime = System.currentTimeMillis();
            // 创建Chat
            getStorageImplementation().insertChat(partyUUID.toString(), ChatType.PARTY, createTime);
            CHAT chat = getStorageImplementation().selectChatWithStartTime(partyUUID.toString(), ChatType.PARTY, createTime);
            int chatID = chat.getId();
            getStorageImplementation().insertParty(partyUUID, leader, createTime, chatID);
            getStorageImplementation().insertData(uuid, DataType.SOCIAL_SYSTEMS, "party", partyUUID.toString(), 0);
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
            DATA td = getStorageImplementation().getSpecifiedData(ut, DataType.SOCIAL_SYSTEMS_PARTY_INVITE, partyUUID.toString());
            if (td != null || members.contains(ut)) {
                SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_HAS_BEEN_INVITED.send(sender);
                return;
            }
            getStorageImplementation().insertData(ut, DataType.SOCIAL_SYSTEMS_PARTY_INVITE, partyUUID.toString(), uuid.toString(), 0);
            getPlugin().getBootstrap().getScheduler().asyncLater(() -> {
                // 获取目标玩家是否接受，如果未接受，则发送邀请玩家过期信息
                DATA inviteData = getStorageImplementation().getSpecifiedData(ut, DataType.SOCIAL_SYSTEMS_PARTY_INVITE, partyUUID.toString());
                if (inviteData != null) {
                    // 邀请已过期
                    getStorageImplementation().deleteDataID(inviteData.getId());
                    getPlugin().getMessagingService().ifPresent(service -> {
                        service.pushNoticeMessage(uuid, NoticeMessage.NoticeType.PARTY_INVITE_EXPIRED, new String[]{ut.toString()});
                    });
                }
            }, 1, TimeUnit.MINUTES);
            getAsyncExecutor().execute(() -> getPlugin().getMessagingService().ifPresent(service -> {
                service.pushNoticeMessage(ut, NoticeMessage.NoticeType.PARTY_ACCEPT, new String[]{uuid.toString(), partyUUID.toString()});
                for (UUID member : members) {
                    service.pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_INVITE, new String[]{uuid.toString(), ut.toString()});
                }
            }));
        } else {
            SocialSystemsMessage.COMMAND_MISC_PARTY_INVITE_NO_PERMISSION.send(sender);
        }
    }

    @CommandMethod("party|p disband")
    public void disband(final @NonNull Player player) {
        UUID uuid = player.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(player);
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
        if (data == null) {
            SocialSystemsMessage.COMMAND_MISC_PARTY_NOT_IN.send(sender);
        } else {
            UUID partyUUID = UUID.fromString(data.getValue());
            PARTY party = getStorageImplementation().selectParty(partyUUID);
            UUID leader = party.getLeader();
            List<UUID> members = party.getMembers();
            if (!members.contains(uuid)) {
                SocialSystemsMessage.COMMAND_MISC_PARTY_TARGET_NOT_IN.send(sender);
                return;
            }
            if (leader.equals(uuid)) {
                long disbandTime = System.currentTimeMillis();
                party.setDisbandTime(disbandTime);
                getStorageImplementation().deleteDataID(data.getId());
                getAsyncExecutor().execute(() -> {
                    getPlugin().getMessagingService().ifPresent(service -> {
                        for (UUID member : members) {
                            service.pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_DISBAND, new String[]{uuid.toString()});
                        }
                    });
                });
            } else {
                MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
            }
        }
    }

    @CommandMethod("party|p kick <target>")
    public void kick(final @NonNull Player player, final @NonNull @Argument("target") String target) {
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
                    SocialSystemsMessage.COMMAND_MISC_PARTY_KICK_NOT_PERMISSION.send(sender, target);
                    return;
                }
                DATA td = getStorageImplementation().getSpecifiedData(ut, DataType.SOCIAL_SYSTEMS, "party");
                getStorageImplementation().deleteDataID(td.getId());
                getAsyncExecutor().execute(() -> {
                    getPlugin().getMessagingService().ifPresent(service -> {
                        members.remove(ut);
                        party.setMembers(members);
                        service.pushNoticeMessage(ut, NoticeMessage.NoticeType.PARTY_BE_KICKED, new String[]{uuid.toString()});
                        members.forEach(member -> {
                            service.pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_KICK, new String[]{ut.toString()});
                        });
                    });
                });
            } else {
                MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
            }
        }
    }

    @CommandMethod("party|p leave")
    public void leave(final @NonNull Player player) {
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
            getStorageImplementation().deleteDataID(data.getId());
            members.remove(uuid);
            party.setMembers(members);
            if (moderators.contains(uuid)) {
                moderators.remove(uuid);
                party.setModerators(moderators);
            }
            getAsyncExecutor().execute(() -> {
                getPlugin().getMessagingService().ifPresent(service -> {
                    service.pushNoticeMessage(uuid, NoticeMessage.NoticeType.PARTY_LEAVE, new String[]{uuid.toString()});
                    members.forEach(member -> {
                        service.pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_LEAVE, new String[]{uuid.toString()});
                    });
                });
            });

        }
    }

    @CommandMethod("party|p kickoffline")
    public void kickoffline(final @NonNull Player player) {
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
                    getPlugin().getMessagingService().ifPresent(service -> {
                        List<UUID> newMembers = members.stream()
                                .filter(this::isOnline)
                                .collect(Collectors.toList());

                        List<UUID> offlineMembers = members.stream()
                                .filter(member -> !isOnline(member))
                                .collect(Collectors.toList());

                        party.setMembers(newMembers);

                        offlineMembers.forEach(offlineMember -> {
                            DATA od = getStorageImplementation().getSpecifiedData(offlineMember, DataType.SOCIAL_SYSTEMS, "party");
                            getStorageImplementation().deleteDataID(od.getId());
                            newMembers.forEach(member -> {
                                service.pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_KICK, new String[]{offlineMember.toString()});
                            });
                        });
                    });
                });
            } else {
                MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
            }
        }
    }

    @CommandMethod("party|p accept <uuid>")
    public void accept(final @NonNull Player player, final @NonNull @Argument("uuid") String pu) {
        UUID uuid = player.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(player);
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
        if (data == null) {
            try {
                UUID partyUUID = UUID.fromString(pu);
                DATA inviteData = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS_PARTY_INVITE, partyUUID.toString());
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
                getAsyncExecutor().execute(() -> {
                    getPlugin().getMessagingService().ifPresent(service -> {
                        for (UUID member : members) {
                            service.pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_JOINED, new String[]{uuid.toString()});
                        }
                    });
                });
            } catch (IllegalArgumentException e) {
                MiscMessage.COMMAND_MISC_INVALID_FORMAT.send(sender, pu);
            }
        } else {
            SocialSystemsMessage.COMMAND_MISC_PARTY_ALREADY_IN_THE_TEAM.send(sender);
        }
    }

    @CommandMethod("party|p list")
    public void list(final @NonNull Player player) {
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

    @CommandMethod("party|p chat <message>")
    public void chat(final @NonNull Player player, final @NonNull @Argument("message") @Greedy String message) {
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
                getPlugin().getMessagingService().ifPresent(service -> {
                    for (UUID member : members) {
                        service.pushChatMessage(member, ChatMessage.ChatMessageType.PARTY, new String[]{uuid.toString(), message});
                    }
                });
            });
        }
    }

    @CommandMethod("party|p warp")
    public void warp(final @NonNull Player player) {
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
            if (leader.equals(uuid)) {
                // TODO warp
                partyWarp(party, getPlugin().getServerName());
            } else if (moderators.contains(uuid)) {
                // TODO warp
                partyWarp(party, getPlugin().getServerName());
            } else {
                MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
            }
        }
    }

    public void partyWarp(PARTY party, String serverName) {
        List<UUID> members = party.getMembers();
        getAsyncExecutor().execute(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                for (UUID member : members) {
                    String serverNow;
                    DATA data = getStorageImplementation().getSpecifiedData(member, DataType.FUNCTION, "server-status");
                    if (data != null) {
                        serverNow = data.getValue();
                        if (!serverName.equalsIgnoreCase(serverNow)) {
                            service.pushConnectServer(member, serverName);
                        }
                    }
                }
            });
        });
    }
}
