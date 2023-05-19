package org.floracore.api.messenger.message.type;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface NoticeMessage extends Message {
    /**
     * 获取接收者的UUID
     *
     * @return 接收者的UUID
     */
    @NonNull UUID getReceiver();

    /**
     * 获取通知类型
     *
     * @return 通知类型
     */
    @NonNull NoticeType getType();

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
         * 举报受理通知
         */
        REPORT_ACCEPTED,
        /**
         * 举报处理通知
         */
        REPORT_PROCESSED,
        REPORT_STAFF_ACCEPTED,
        REPORT_STAFF_PROCESSED,
        /**
         * 组队解散
         */
        PARTY_DISBAND,
        PARTY_ACCEPT,
        PARTY_INVITE,
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
        PARTY_PROMOTE_MODERATOR,
        PARTY_OFFLINE_LEADER,
        PARTY_OFFLINE,
        PARTY_OFFLINE_KICK,
        PARTY_OFFLINE_RE_ONLINE,
        PARTY_OFFLINE_TRANSFER,
        PARTY_DEMOTE,
    }
}
