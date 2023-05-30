package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import org.floracore.api.player.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ApiPlayer implements PlayerAPI {
    private static final Cache<UUID, PLAYER> playersCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS).build();
    private static final Cache<String, PLAYER> playerRecordCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS).build();
    private final FloraCorePlugin plugin;

    public ApiPlayer(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPlayerRecord(String name) {
        return getPlayers(name) != null;
    }

    public PLAYER getPlayers(String name) {
        PLAYER player = playerRecordCache.getIfPresent(name);
        if (player == null) {
            player = plugin.getStorage().getImplementation().selectPlayer(name);
            playerRecordCache.put(name, player);
        }
        return player;
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
        PLAYER player = playersCache.getIfPresent(uuid);
        if (player == null) {
            player = plugin.getStorage().getImplementation().selectPlayer(uuid);
            playersCache.put(uuid, player);
        }
        return player;
    }

    @Override
    public boolean isOnline(UUID uuid) {
        List<Sender> senders = plugin.getOnlineSenders().collect(Collectors.toList());
        for (Sender sender : senders) {
            if (sender.getUniqueId() == uuid) {
                return true;
            }
        }
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
