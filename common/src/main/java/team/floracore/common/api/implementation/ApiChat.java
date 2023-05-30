package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import com.google.gson.reflect.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;
import team.floracore.common.util.gson.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ApiChat implements ChatAPI {
    private static final Cache<UUID, List<DATA>> chatDataCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS).build();
    private static final Cache<UUID, List<DATA>> partyDataCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS).build();
    private static final Cache<UUID, PARTY> partyCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS).build();
    private final FloraCorePlugin plugin;

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
        List<DATA> data = chatDataCache.getIfPresent(uuid);
        if (data == null) {
            data = plugin.getStorage().getImplementation().getSpecifiedTypeData(uuid, DataType.CHAT);
            chatDataCache.put(uuid, data);
        }
        return data;
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
        List<DATA> data = partyDataCache.getIfPresent(uuid);
        if (data == null) {
            data = plugin.getStorage().getImplementation().getSpecifiedTypeData(uuid, DataType.SOCIAL_SYSTEMS_PARTY_HISTORY);
            partyDataCache.put(uuid, data);
        }
        return data;
    }

    public PARTY getPlayerPartyData(UUID uuid) {
        PARTY data = partyCache.getIfPresent(uuid);
        if (data == null) {
            data = plugin.getStorage().getImplementation().selectParty(uuid);
            partyCache.put(uuid, data);
        }
        return data;
    }
}
