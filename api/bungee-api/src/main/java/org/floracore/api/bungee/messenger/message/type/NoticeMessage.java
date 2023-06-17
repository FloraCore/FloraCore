package org.floracore.api.bungee.messenger.message.type;

import org.floracore.api.messenger.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * 通知消息
 */
public interface NoticeMessage extends Message {
    /**
     * 获取接收者的UUID
     *
     * @return 接收者的UUID
     */
    @NotNull UUID getReceiver();

    /**
     * 获取通知类型
     *
     * @return 通知类型
     */
    @NotNull NoticeType getType();

    /**
     * 获取参数
     *
     * @return 参数
     */
    @NotNull List<String> getParameters();

    /**
     * 通知类型
     */
    enum NoticeType {
        /**
         * 组队解散
         */
        PARTY_DISBAND,
        /**
         * 同意组队的邀请通知
         */
        PARTY_ACCEPT,
        /**
         * 组队邀请的通知
         * 接受该消息的玩家是被邀请的玩家
         */
        PARTY_INVITE,
        /**
         * 组队邀请已过期的通知
         * 接受该消息的玩家是发起该邀请的玩家
         */
        PARTY_INVITE_EXPIRED,
        /**
         * 组队加入
         */
        PARTY_JOINED,
        /**
         * 踢出组队
         */
        PARTY_KICK,
        /**
         * 已被踢出
         */
        PARTY_BE_KICKED,
        /**
         * 组队离开
         */
        PARTY_LEAVE,
        /**
         * 组队传送（通过队长）
         */
        PARTY_WARP_LEADER,
        /**
         * 组队传送（通过管理员）
         */
        PARTY_WARP_MODERATOR,
        /**
         * 队长转让给了另一名玩家
         */
        PARTY_PROMOTE_LEADER,
        /**
         * 组队队员的权限提升至管理员
         */
        PARTY_PROMOTE_MODERATOR,
        /**
         * 当队长离线时的通知
         */
        PARTY_OFFLINE_LEADER,
        /**
         * 当组队成员（非队长）离线时的通知
         */
        PARTY_OFFLINE,
        /**
         * 当组队离线成员被踢出组队时的通知
         */
        PARTY_OFFLINE_KICK,
        /**
         * 组队离线成员重新上线时的通知
         */
        PARTY_OFFLINE_RE_ONLINE,
        /**
         * 组队离线队长转让的通知
         */
        PARTY_OFFLINE_TRANSFER,
        /**
         * 组队队员降职通知
         */
        PARTY_DEMOTE,
    }
}
