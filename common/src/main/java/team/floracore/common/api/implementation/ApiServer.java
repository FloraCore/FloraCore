package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import org.floracore.api.server.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.concurrent.*;

/**
 * 服务器API的实现
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/26 17:57
 */
public class ApiServer implements ServerAPI {
    private final FloraCorePlugin plugin;
    AsyncCache<String, SERVER> serverCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(10000)
            .buildAsync();

    public ApiServer(FloraCorePlugin plugin) {this.plugin = plugin;}

    @Override
    public ServerType getServerType(String serverName) {
        SERVER server = getServerData(serverName);
        if (server == null) {
            return null;
        }
        return server.getType();
    }

    public SERVER getServerData(String name) {
        CompletableFuture<SERVER> data = serverCache.get(name,
                u -> plugin.getStorage().getImplementation().selectServer(name));
        serverCache.put(name, data);
        return data.join();
    }
}
