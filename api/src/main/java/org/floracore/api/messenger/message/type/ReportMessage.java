package org.floracore.api.messenger.message.type;


import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.*;

import java.util.*;

public interface ReportMessage extends Message {

    /**
     * 获取举报者的UUID。
     *
     * @return 举报者的UUID
     */
    @NonNull UUID getReporter();

    /**
     * 获取被举报者的UUID。
     *
     * @return 被举报者的UUID
     */
    @NonNull UUID getReportedUser();

    /**
     * 获取被举报者所在的服务器。
     *
     * @return 被举报者所在的服务器
     */
    @NonNull String getReportedUserServer();

    /**
     * 获取举报者所在的服务器。
     *
     * @return 举报者所在的服务器
     */
    @NonNull String getReporterServer();
}
