package team.floracore.bungee.locale.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import org.floracore.api.FloraCoreProvider;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;

import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface SocialSystemsMessage extends AbstractMessage {
    Args1<String> COMMAND_MISC_PARTY_INVITE_EXPIRED = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable("floracore.command.misc.party.invite.expired", AQUA).args(text(target, GRAY)),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_NO_PERMISSION = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你没有发送组队邀请的权限!
                        .key("floracore.command.misc.party.invite.no-permission").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_SELF = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你不能向自己发送组队邀请!
                        .key("floracore.command.misc.party.invite.self").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_HAS_BEEN_INVITED = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 这名玩家已经被邀请到组队中了!
                        .key("floracore.command.misc.party.invite.has-been-invited").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_ALREADY_IN_THE_TEAM = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你已经在组队里了,必须离开当前组队！
                        .key("floracore.command.misc.party.already-in-the-team").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_ALREADY_IN_THE_TEAM = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 这名玩家已经在组队里了!
                        .key("floracore.command.misc.party.invite.already-in-the-team").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_ALREADY_IN_THE_OTHER_TEAM = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 这名玩家已经有自己的组队了!
                        .key("floracore.command.misc.party.invite.already-in-the-other-team").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_ALREADY_JOINED_THE_TEAM = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你已经加入了这个队伍,不能重复加入!
                        .key("floracore.command.misc.party.already_joined_the_team").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVALID = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 这个组队不存在或已被解散!
                        .key("floracore.command.misc.party.invalid").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_NOT_INVITED = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你没有被邀请加入到这个组队中!
                        .key("floracore.command.misc.party.not-invited").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args2<String, String> COMMAND_MISC_PARTY_INVITE = (sender, target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已邀请 {1} 到组队中!他们有 {2} 秒时间接受邀请
                        .key("floracore.command.misc.party.invite").color(AQUA)
                        // {}
                        .args(text(sender, GRAY), text(target, GRAY), text(60, RED)).append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args2<String, UUID> COMMAND_MISC_PARTY_INVITE_ACCEPT = (sender, partyUUID) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/party accept " + partyUUID.toString());
        Component click = MiscMessage.CLICK_JOIN;
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已经邀请你加入他们的组队!你有 {1} 秒的时间来接受。{2}
                        .key("floracore.command.misc.party.invite.accept").color(AQUA)
                        // {}
                        .args(text(sender, GRAY), text(60, RED), click).clickEvent(clickEvent).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_NOT_IN = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你当前不在组队中
                        .key("floracore.command.misc.party.not-in").color(RED).append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_WARP_NOT_ENOUGH_PEOPLE = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 组队中没有玩家可供传送
                        .key("floracore.command.misc.party.warp.not-enough-people").color(RED).append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_WARP_SUCCESS = (members) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你将 {0} 传送到你的服务器
                        .key("floracore.command.misc.party.warp.success").color(AQUA)
                        .args(text(members, GRAY))
                        .append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_WARP_MODERATOR = (moderator) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 组队管理员 {0} 召集你到其所在的服务器
                        .key("floracore.command.misc.party.warp.moderator").color(AQUA)
                        .args(text(moderator, GRAY))
                        .append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_WARP_LEADER = (leader) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 组队队长 {0} 召集你到其所在的服务器
                        .key("floracore.command.misc.party.warp.leader").color(AQUA)
                        .args(text(leader, GRAY))
                        .append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_TRANSFER_NO_PERMISSION = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你不是组队队长！
                        .key("floracore.command.misc.party.transfer.no-permission").color(RED).append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args0 COMMAND_MISC_PARTY_TRANSFER_SELF = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你不是组队队长！
                        .key("floracore.command.misc.party.transfer.self").color(RED).append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args2<String, String> COMMAND_MISC_PARTY_PROMOTE_LEADER = (sender, target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已将 {1} 提拔为组队队长,{0} 现在为组队管理员
                        .key("floracore.command.misc.party.promote.leader").color(AQUA)
                        .append(FULL_STOP)
                        // {}
                        .args(text(sender, GRAY), text(target, GRAY))
                        .build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args2<String, String> COMMAND_MISC_PARTY_PROMOTE_MODERATOR = (sender, target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已将 {1} 提拔为组队管理员
                        .key("floracore.command.misc.party.promote.moderator").color(AQUA)
                        // {}
                        .args(text(sender, GREEN), text(target, DARK_GREEN))
                        .build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args2<String, String> COMMAND_MISC_PARTY_DEMOTE = (sender, target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已将 {1} 降职为普通成员
                        .key("floracore.command.misc.party.demote").color(RED)
                        // {}
                        .args(text(sender, GRAY), text(target, GRAY))
                        .build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args1<String> COMMAND_MISC_PARTY_DEMOTE_ALREADY_IN = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已经是普通成员
                        .key("floracore.command.misc.party.demote.already-in").color(RED)
                        // {}
                        .args(text(target, GRAY))
                        .build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args1<String> COMMAND_MISC_PARTY_OFFLINE_LEADER = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 组队队长 {0} 已断开连接,若 {1} 分钟内未重新连接则移出组队
                        .key("floracore.command.misc.party.offline.leader").append(FULL_STOP)
                        // {}
                        .args(text(target, GRAY), text(5, RED)).color(AQUA).build(),
                translatable().key("floracore.command.misc.party.offline.leader.transfer.1")
                        .color(GRAY)
                        .append(FULL_STOP)
                        .append(translatable("floracore.command.misc.party.offline.leader.transfer.2"))
                        .color(GRAY)
                        .append(FULL_STOP)
                        .append(translatable("floracore.command.misc.party.offline.leader.transfer.3"))
                        .color(GRAY)
                        .append(FULL_STOP)
                        .color(AQUA)
                        .build()
                , MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args2<String, String> COMMAND_MISC_PARTY_OFFLINE_TRANSFER = (offlinePlayer, target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 由于 {0} 已断开连接,且 {1} 分钟内未重新连接,现已将队长转让给 {2}
                        .key("floracore.command.misc.party.offline.transfer").color(AQUA)
                        // {}
                        .args(text(offlinePlayer, GRAY), text(5, RED), text(target, GRAY))
                        .build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args1<String> COMMAND_MISC_PARTY_OFFLINE_KICK = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 由于 {0} 已断开连接,且 {1} 分钟内未重新连接,现已将其移出组队
                        .key("floracore.command.misc.party.offline.kick").append(FULL_STOP).color(AQUA)
                        // {}
                        .args(text(target, GRAY), text(5, RED))
                        .build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args1<String> COMMAND_MISC_PARTY_OFFLINE = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已断开连接,若 {1} 分钟内未重新连接则移出组队
                        .key("floracore.command.misc.party.offline").append(FULL_STOP)
                        // {}
                        .args(text(target, GRAY), text(5, RED)).color(AQUA).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args1<String> COMMAND_MISC_PARTY_OFFLINE_RE_ONLINE = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已恢复连接
                        .key("floracore.command.misc.party.offline.re-online").append(FULL_STOP)
                        // {}
                        .args(text(target, GRAY)).color(AQUA).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build()
        );
    };

    Args0 COMMAND_MISC_PARTY_TARGET_NOT_IN = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 这名玩家当前不在组队中
                        .key("floracore.command.misc.party.target-not-in").color(RED).append(FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_DISBAND = (sender) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // {0} 解散了组队!
                        .key("floracore.command.misc.party.disband").color(AQUA)
                        // {}
                        .args(text(sender, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_LEAVE = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // {0} 离开了组队
                        .key("floracore.command.misc.party.leave").color(AQUA)
                        // {}
                        .args(text(target, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_KICK = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // {0} 被移出了组队
                        .key("floracore.command.misc.party.kick").color(AQUA)
                        // {}
                        .args(text(target, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_BE_KICKED = (sender) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // 你已被 {0} 移出了组队
                        .key("floracore.command.misc.party.be-kicked").color(AQUA)
                        // {}
                        .args(text(sender, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_KICK_SELF = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // 你不能将自己移出组队!
                        .key("floracore.command.misc.party.kick.self").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_KICK_NOT_PERMISSION = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // 你不能将 {0} 移出组队!
                        .key("floracore.command.misc.party.kick.no-permission")
                        // {}
                        .args(text(target, DARK_RED)).color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_KICKOFFLINE_NO_MEMBERS_AVAILABLE = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // 组队中没有符合条件的成员离线!
                        .key("floracore.command.misc.party.kickoffline.no-members-available").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_LEAVE_IS_LEADER = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // 你是组队的队长,不能离开队伍!
                        .key("floracore.command.misc.party.leave.is-leader").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_JOIN = (sender) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // {0} 加入了组队
                        .key("floracore.command.misc.party.join").append(FULL_STOP).color(AQUA)
                        // {}
                        .args(text(sender, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args2<String, String> COMMAND_MISC_PARTY_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_PARTY).append(space())
            .append(text(sender, GRAY))
            .append(COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();
    Args2<String, String> COMMAND_MISC_STAFF_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_STAFF).append(space())
            .append(text(sender, GRAY))
            .append(COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();

    Args2<String, String> COMMAND_MISC_BLOGGER_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_BLOGGER).append(space())
            .append(text(sender, GRAY))
            .append(COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();

    Args2<String, String> COMMAND_MISC_BUILDER_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_BUILDER).append(space())
            .append(text(sender, GRAY))
            .append(COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();

    Args2<String, String> COMMAND_MISC_ADMIN_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_ADMIN).append(space())
            .append(text(sender, GRAY))
            .append(COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();
    Args3<UUID, List<UUID>, List<UUID>> COMMAND_MISC_PARTY_LIST = (leader, moderators, members) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component title = translatable("floracore.command.misc.party.list")
                .args(OPEN_BRACKET.append(text(members.size())).append(CLOSE_BRACKET)).color(AQUA);
        String leaderName = FloraCoreProvider.get().getPlayerAPI().getPlayerRecordName(leader);
        boolean leaderOnline = FloraCoreProvider.get().getPlayerAPI().isOnline(leader);
        Component leaderComponent = translatable("floracore.command.misc.party.leader")
                // {}
                .args(text(leaderName, GRAY).append(space()).append(CIRCLE.color(leaderOnline ? GREEN : RED)))
                .color(GREEN);
        Component c = join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                title,
                space(),
                leaderComponent);
        members.remove(leader);
        if (!moderators.isEmpty()) {
            Component mc = Component.empty();
            for (UUID moderator : moderators) {
                String moderatorName = FloraCoreProvider.get().getPlayerAPI().getPlayerRecordName(moderator);
                boolean moderatorOnline = FloraCoreProvider.get().getPlayerAPI().isOnline(moderator);
                mc = mc.append(text(moderatorName, GRAY).append(space())
                                .append(CIRCLE.color(moderatorOnline ? GREEN : RED)))
                        .append(space());
                members.remove(moderator);
            }
            c = join(joinConfig, c, space(),
                    translatable().key("floracore.command.misc.party.moderators")
                            // {}
                            .args(mc).color(GREEN).build());
        }
        if (!members.isEmpty()) {
            Component mc = Component.empty();
            for (UUID member : members) {
                String memberName = FloraCoreProvider.get().getPlayerAPI().getPlayerRecordName(member);
                boolean memberOnline = FloraCoreProvider.get().getPlayerAPI().isOnline(member);
                mc = mc.append(text(memberName, GRAY).append(space()).append(CIRCLE.color(memberOnline ? GREEN : RED)))
                        .append(space());
            }
            c = join(joinConfig, c, space(),
                    translatable().key("floracore.command.misc.party.members")
                            // {}
                            .args(mc).color(GREEN).build());
        }
        c = join(joinConfig, c, MiscMessage.PARTY_HORIZONTAL_LINE.build());
        return c;
    };

    Args1<String> COMMAND_MISC_CHAT_DOES_NOT_EXIST = (type) -> AbstractMessage.prefixed(translatable()
            // 不存在 {0} 这个聊天频道!
            .key("floracore.command.misc.chat.does-not-exist")
            // {0}
            .args(text(type,
                    DARK_RED))
            .color(RED));

    Args1<Component> COMMAND_MISC_CHAT_SUCCESS = (type) -> AbstractMessage.prefixed(translatable()
            // 成功切换到 {0} 聊天频道!
            .key("floracore.command.misc.chat.success")
            // {0}
            .args(type).color(AQUA));

    Args1<Component> COMMAND_MISC_CHAT_IS_IN = (type) -> AbstractMessage.prefixed(translatable()
            // 你当前正处于 {0} 聊天频道中!
            .key("floracore.command.misc.chat.is-in")
            // {0}
            .args(type)
            .color(RED));
}
