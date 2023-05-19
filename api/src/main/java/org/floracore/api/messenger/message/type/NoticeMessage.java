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
        PARTY_DISBAND,
        PARTY_ACCEPT,
        PARTY_INVITE,
        PARTY_INVITE_EXPIRED,
        PARTY_JOINED,
        PARTY_KICK,
        PARTY_BE_KICKED,
        PARTY_LEAVE,
        PARTY_WARP_LEADER,
        PARTY_WARP_MODERATOR,
    }
}
