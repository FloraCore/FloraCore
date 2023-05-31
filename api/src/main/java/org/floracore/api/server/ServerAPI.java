package org.floracore.api.server;

/**
 * FloraCore服务器API
 *
 * @author xLikeWATCHDOG
 */
public interface ServerAPI {
    /**
     * 通过服务器名获取服务器类型。
     * 若不存在服务器,则返回null。
     *
     * @param serverName 服务器名
     *
     * @return 服务器类型
     */
    ServerType getServerType(String serverName);
}
