package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.Cache;
import org.floracore.api.data.DataAPI;
import org.floracore.api.data.DataType;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.misc.floracore.tables.DATA;
import team.floracore.common.storage.misc.floracore.tables.DATA_INT;
import team.floracore.common.storage.misc.floracore.tables.PLAYER;
import team.floracore.common.util.CaffeineFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ApiData implements DataAPI {
    private static final Cache<String, SortedMap<UUID, Integer>> sortedDataIntCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(5,
                    TimeUnit.SECONDS)
            .build();
    private final FloraCorePlugin plugin;

    public ApiData(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getSpecifiedDataValue(UUID uuid, DataType type, String key) {
        DATA data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getValue();
    }

    @Override
    public Integer getSpecifiedDataIntValue(UUID uuid, DataType type, String key) {
        DATA_INT data = getSpecifiedDataInt(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getValue();
    }

    public DATA getSpecifiedData(UUID uuid, DataType type, String key) {
        return plugin.getStorage().getImplementation().getSpecifiedData(uuid, type, key);
    }

    public DATA_INT getSpecifiedDataInt(UUID uuid, DataType type, String key) {
        return plugin.getStorage().getImplementation().getSpecifiedDataInt(uuid, type, key);
    }

    @Override
    public Long getSpecifiedDataExpiry(UUID uuid, DataType type, String key) {
        DATA data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getExpiry();
    }

    @Override
    public Long getSpecifiedDataIntExpiry(UUID uuid, DataType type, String key) {
        DATA_INT data = getSpecifiedDataInt(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getExpiry();
    }

    @Override
    public Integer getSpecifiedDataID(UUID uuid, DataType type, String key) {
        DATA data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            return null;
        }
        return data.getId();
    }

    @Override
    public Integer getSpecifiedDataIntID(UUID uuid, DataType type, String key) {
        DATA_INT data = getSpecifiedDataInt(uuid, type, key);
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
    public int insertDataInt(UUID uuid, DataType type, String key, int value, long expiry) {
        return plugin.getStorage().getImplementation().insertDataInt(uuid, type, key, value, expiry).getId();
    }

    @Override
    public void deleteData(int id) {
        plugin.getStorage().getImplementation().deleteDataID(id);
    }

    @Override
    public void deleteDataInt(int id) {
        plugin.getStorage().getImplementation().deleteDataIntID(id);
    }

    @Override
    public void deleteDataAll(UUID uuid) {
        plugin.getStorage().getImplementation().deleteDataAll(uuid);
    }

    @Override
    public void deleteDataIntAll(UUID uuid) {
        plugin.getStorage().getImplementation().deleteDataIntAll(uuid);
    }

    @Override
    public void deleteDataType(UUID uuid, DataType type) {
        plugin.getStorage().getImplementation().deleteDataType(uuid, type);
    }

    @Override
    public void deleteDataIntType(UUID uuid, DataType type) {
        plugin.getStorage().getImplementation().deleteDataIntType(uuid, type);
    }

    @Override
    public void deleteDataExpired(UUID uuid) {
        plugin.getStorage().getImplementation().deleteDataExpired(uuid);
    }

    @Override
    public void deleteDataIntExpired(UUID uuid) {
        plugin.getStorage().getImplementation().deleteDataIntExpired(uuid);
    }

    @Override
    public SortedMap<UUID, Integer> getDataIntSorted(DataType dataType, String key, boolean ascending) {
        SortedMap<UUID, Integer> ret = sortedDataIntCache.getIfPresent(dataType.name() + key);
        if (ret == null) {
            List<DATA_INT> i = plugin.getStorage().getImplementation().selectDataIntSorted(dataType, key, ascending);
            SortedMap<UUID, Integer> sortedMap = new TreeMap<>();
            i.forEach(data -> sortedMap.put(data.getUniqueId(), data.getValue()));
            ret = sortedMap;
            sortedDataIntCache.put(dataType.name() + key, ret);
        }
        return ret;
    }


    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}
