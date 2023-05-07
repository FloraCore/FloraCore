package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import org.floracore.api.data.*;
import org.floracore.api.player.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.concurrent.*;

public class ApiPlayer implements PlayerAPI {
    private final FloraCorePlugin plugin;
    AsyncCache<UUID, Players> playersCache = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).maximumSize(10000).buildAsync();
    AsyncCache<String, Players> playersRecordCache = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).maximumSize(10000).buildAsync();

    public ApiPlayer(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public Players getPlayers(UUID uuid) {
        CompletableFuture<Players> players = playersCache.get(uuid, u -> plugin.getStorage().getImplementation().selectPlayers(u));
        playersCache.put(uuid, players);
        return players.join();
    }

    public Players getPlayers(String name) {
        CompletableFuture<Players> players = playersRecordCache.get(name, n -> plugin.getStorage().getImplementation().selectPlayers(n));
        playersRecordCache.put(name, players);
        return players.join();
    }

    @Override
    public boolean hasPlayerRecord(String name) {
        return getPlayers(name) != null;
    }

    @Override
    public UUID getPlayerRecordUUID(String name) {
        Players players = getPlayers(name);
        if (players == null) {
            return null;
        }
        return players.getUuid();
    }

    @Override
    public String getPlayerRecordName(UUID uuid) {
        Players players = getPlayers(uuid);
        if (players == null) {
            return null;
        }
        return players.getName();
    }

    @Override
    public boolean isOnline(UUID uuid) {
        String status = plugin.getApiProvider().getDataAPI().getSpecifiedDataValue(uuid, DataType.FUNCTION, "online-status");
        if (status == null) {
            return false;
        }
        return Boolean.parseBoolean(status);
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}
