package org.floracore.api.server;

/**
 * FloraCore服务器API
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/26 17:56
 */
public interface ServerAPI {
    ServerType getServerType(String serverName);
}
