package org.floracore.api.bukkit.messenger.message.type;

import org.floracore.api.messenger.message.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ReportMessage extends Message {

    /**
     * 获取举报者的UUID。
     *
     * @return 举报者的UUID
     */
    @NotNull UUID getReporter();

    /**
     * 获取被举报者的UUID。
     *
     * @return 被举报者的UUID
     */
    @NotNull UUID getReportedUser();

    /**
     * 获取被举报者所在的服务器。
     *
     * @return 被举报者所在的服务器
     */
    @NotNull String getReportedUserServer();

    /**
     * 获取举报者所在的服务器。
     *
     * @return 举报者所在的服务器
     */
    @NotNull String getReporterServer();

    /**
     * 获取举报原因。
     *
     * @return 举报原因
     */
    @NotNull String getReason();
}
