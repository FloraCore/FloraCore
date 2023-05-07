package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.*;
import org.floracore.api.data.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.concurrent.*;

public class ApiData implements DataAPI {
    private final FloraCorePlugin plugin;
    AsyncCache<UUID, Data> dataCache = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).maximumSize(10000).buildAsync();

    public ApiData(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public Data getSpecifiedData(UUID uuid, DataType type, String key) {
        CompletableFuture<Data> data = dataCache.get(uuid, u -> plugin.getStorage().getImplementation().getSpecifiedData(u, type, key));
        dataCache.put(uuid, data);
        return data.join();
    }

    @Override
    public String getSpecifiedDataValue(UUID uuid, DataType type, String key) {
        Data data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getValue();
    }

    @Override
    public Long getSpecifiedDataExpiry(UUID uuid, DataType type, String key) {
        Data data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getExpiry();
    }

    @Override
    public Integer getSpecifiedDataID(UUID uuid, DataType type, String key) {
        Data data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getId();
    }

    @Override
    public int insertData(UUID uuid, DataType type, String key, String value, long expiry) {
        return plugin.getStorage().getImplementation().insertData(uuid, type, key, value, expiry).getId();
    }

    @Override
    public void deleteData(int id) {
        plugin.getStorage().getImplementation().deleteDataID(id);
    }

    @Override
    public void deleteDataAll(UUID uuid) {
        plugin.getStorage().getImplementation().deleteDataAll(uuid);
    }

    @Override
    public void deleteDataType(UUID uuid, DataType type) {
        plugin.getStorage().getImplementation().deleteDataType(uuid, type);
    }

    @Override
    public void deleteDataExpired(UUID uuid) {
        plugin.getStorage().getImplementation().deleteDataExpired(uuid);
    }


    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}
