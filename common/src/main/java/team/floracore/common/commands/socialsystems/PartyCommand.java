package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import org.bukkit.entity.*;
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

@CommandContainer
@CommandDescription("组队是一个社交系统。玩家可以与其他玩家一起游玩")
public class PartyCommand extends AbstractFloraCoreCommand {
    public PartyCommand(FloraCorePlugin plugin) {
        super(plugin);
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
            PARTY party = getStorageImplementation().selectParty(partyUUID);
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
                MiscMessage.PARTY_HORIZONTAL_LINE.send(sender);
                Message.COMMAND_MISC_PARTY_INVITE_SELF.send(sender);
                MiscMessage.PARTY_HORIZONTAL_LINE.send(sender);
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
            getAsyncExecutor().execute(() -> {
                getPlugin().getMessagingService().ifPresent(service -> {
                    // TODO 向target发送被邀请的通知
                    for (UUID member : members) {
                        service.pushNoticeMessage(member, NoticeMessage.NoticeType.PARTY_INVITE, new String[]{uuid.toString(), ut.toString()});
                    }
                });
            });
        } else {
            MiscMessage.PARTY_HORIZONTAL_LINE.send(sender);
            Message.COMMAND_MISC_PARTY_INVITE_NO_PERMISSION.send(sender);
            MiscMessage.PARTY_HORIZONTAL_LINE.send(sender);
        }
    }

    @CommandMethod("party|p disband")
    public void disband(final @NonNull Player player) {
        UUID uuid = player.getUniqueId();
        Sender sender = getPlugin().getSenderFactory().wrap(player);
        DATA data = getStorageImplementation().getSpecifiedData(uuid, DataType.SOCIAL_SYSTEMS, "party");
        if (data == null) {
            MiscMessage.PARTY_HORIZONTAL_LINE.send(sender);
            Message.COMMAND_MISC_PARTY_NOT_IN.send(sender);
            MiscMessage.PARTY_HORIZONTAL_LINE.send(sender);
        } else {
            // TODO 解散
            UUID partyUUID = UUID.fromString(data.getValue());
            PARTY party = getStorageImplementation().selectParty(partyUUID);
            List<UUID> members = party.getMembers();
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
        }
    }
}
