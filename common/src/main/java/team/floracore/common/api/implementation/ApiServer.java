package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import org.floracore.api.server.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;

import java.util.concurrent.*;

/**
 * 服务器API的实现
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/26 17:57
 */
public class ApiServer implements ServerAPI {
    private static final Cache<String, SERVER> serverCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS).build();
    private final FloraCorePlugin plugin;

    public ApiServer(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ServerType getServerType(String serverName) {
        SERVER server = getServerData(serverName);
        if (server == null) {
            return null;
        }
        return server.getType();
    }

    public SERVER getServerData(String name) {
        SERVER server = serverCache.getIfPresent(name);
        if (server == null) {
            server = plugin.getStorage().getImplementation().selectServer(name);
            if (server == null){
                return null;
            }
            serverCache.put(name, server);
        }
        return server;
    }
}
