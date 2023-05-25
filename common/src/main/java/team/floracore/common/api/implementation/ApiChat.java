package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import com.google.gson.reflect.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.gson.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ApiChat implements ChatAPI {
    private final FloraCorePlugin plugin;
    AsyncCache<UUID, List<DATA>> chatDataCache = Caffeine.newBuilder()
                                                         .expireAfterWrite(3, TimeUnit.SECONDS)
                                                         .maximumSize(10000)
                                                         .buildAsync();
    AsyncCache<UUID, List<DATA>> partyDataCache = Caffeine.newBuilder()
                                                          .expireAfterWrite(3, TimeUnit.SECONDS)
                                                          .maximumSize(10000)
                                                          .buildAsync();
    AsyncCache<UUID, PARTY> partyCache = Caffeine.newBuilder()
                                                 .expireAfterWrite(3, TimeUnit.SECONDS)
                                                 .maximumSize(10000)
                                                 .buildAsync();

    public ApiChat(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public List<DataChatRecord> getPlayerChatUUIDRecent(UUID uuid, int number) {
        List<DATA> i = getPlayerChatData(uuid);
        List<DataChatRecord> ret = new ArrayList<>();
        for (DATA data : i) {
            String value = data.getValue();
            if (value.isEmpty()) {
                continue;
            }
            Type type = new TypeToken<DataChatRecord>() {
            }.getType();
            DataChatRecord records = GsonProvider.normal().fromJson(value, type);
            ret.add(records);
        }
        return ret.stream()
                  .sorted(Comparator.comparingLong(DataChatRecord::getJoinTime).reversed())
                  .limit(number)
                  .collect(Collectors.toList());
    }

    public List<DATA> getPlayerChatData(UUID uuid) {
        CompletableFuture<List<DATA>> data = chatDataCache.get(uuid,
                u -> plugin.getStorage().getImplementation().getSpecifiedTypeData(u, DataType.CHAT));
        chatDataCache.put(uuid, data);
        return data.join();
    }

    @Override
    public List<DataChatRecord> getPlayerChatRecentParty(UUID uuid, int number) {
        List<DATA> i = getPlayerPartyChatData(uuid);
        List<DataChatRecord> ret = new ArrayList<>();
        for (DATA data : i) {
            String value = data.getValue();
            if (value.isEmpty()) {
                continue;
            }
            UUID partyUUID = UUID.fromString(value);
            PARTY party = getPlayerPartyData(partyUUID);
            DataChatRecord records = new DataChatRecord(party.getChat(),
                    party.getCreateTime(),
                    System.currentTimeMillis());
            ret.add(records);
        }
        return ret.stream()
                  .sorted(Comparator.comparingLong(DataChatRecord::getJoinTime).reversed())
                  .limit(number)
                  .collect(Collectors.toList());
    }

    public List<DATA> getPlayerPartyChatData(UUID uuid) {
        CompletableFuture<List<DATA>> data = partyDataCache.get(uuid,
                u -> plugin.getStorage()
                           .getImplementation()
                           .getSpecifiedTypeData(u, DataType.SOCIAL_SYSTEMS_PARTY_HISTORY));
        partyDataCache.put(uuid, data);
        return data.join();
    }

    public PARTY getPlayerPartyData(UUID uuid) {
        CompletableFuture<PARTY> data = partyCache.get(uuid,
                u -> plugin.getStorage().getImplementation().selectParty(u));
        partyCache.put(uuid, data);
        return data.join();
    }
}
