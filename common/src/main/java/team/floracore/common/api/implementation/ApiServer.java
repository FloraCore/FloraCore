package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.Cache;
import org.floracore.api.server.ServerAPI;
import org.floracore.api.server.ServerType;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.misc.floracore.tables.SERVER;
import team.floracore.common.util.CaffeineFactory;

import java.util.concurrent.TimeUnit;

/**
 * 服务器API的实现
 *
 * @author xLikeWATCHDOG
 */
public class ApiServer implements ServerAPI {
	private static final Cache<String, SERVER> serverCache = CaffeineFactory.newBuilder()
			.expireAfterWrite(10, TimeUnit.SECONDS)
			.build();
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
			if (server == null) {
				return null;
			}
			serverCache.put(name, server);
		}
		return server;
	}
}
