package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import com.google.gson.reflect.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import team.floracore.common.locale.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.gson.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ApiChat implements ChatAPI {
    private final FloraCorePlugin plugin;
    private final ChatManager chatManager;
    AsyncCache<UUID, List<DATA>> chatDataCache = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).maximumSize(10000).buildAsync();

    public ApiChat(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public List<DATA> getPlayerChatData(UUID uuid) {
        CompletableFuture<List<DATA>> data = chatDataCache.get(uuid, u -> plugin.getStorage().getImplementation().getSpecifiedTypeData(u, DataType.CHAT));
        chatDataCache.put(uuid, data);
        return data.join();
    }

    @Override
    public UUID getPlayerChatUUID(UUID uuid) {
        return chatManager.getPlayerChatUUID(uuid);
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
}
