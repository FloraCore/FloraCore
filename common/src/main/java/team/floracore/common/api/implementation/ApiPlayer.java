package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import org.floracore.api.player.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.concurrent.*;

public class ApiPlayer implements PlayerAPI {
    private final FloraCorePlugin plugin;
    AsyncCache<UUID, PLAYER> playersCache = Caffeine.newBuilder()
                                                    .expireAfterWrite(3, TimeUnit.SECONDS)
                                                    .maximumSize(10000)
                                                    .buildAsync();
    AsyncCache<String, PLAYER> playersRecordCache = Caffeine.newBuilder()
                                                            .expireAfterWrite(3, TimeUnit.SECONDS)
                                                            .maximumSize(10000)
                                                            .buildAsync();

    public ApiPlayer(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPlayerRecord(String name) {
        return getPlayers(name) != null;
    }

    public PLAYER getPlayers(String name) {
        CompletableFuture<PLAYER> players = playersRecordCache.get(name,
                n -> plugin.getStorage().getImplementation().selectPlayer(n));
        playersRecordCache.put(name, players);
        return players.join();
    }

    @Override
    public UUID getPlayerRecordUUID(String name) {
        PLAYER players = getPlayers(name);
        if (players == null) {
            return null;
        }
        return players.getUniqueId();
    }

    @Override
    public String getPlayerRecordName(UUID uuid) {
        PLAYER players = getPlayers(uuid);
        if (players == null) {
            return null;
        }
        return players.getName();
    }

    public PLAYER getPlayers(UUID uuid) {
        CompletableFuture<PLAYER> players = playersCache.get(uuid,
                u -> plugin.getStorage().getImplementation().selectPlayer(u));
        playersCache.put(uuid, players);
        return players.join();
    }

    @Override
    public boolean isOnline(UUID uuid) {
        ONLINE online = plugin.getStorage().getImplementation().selectOnline(uuid);
        if (online == null) {
            return false;
        }
        return online.getStatus();
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}
