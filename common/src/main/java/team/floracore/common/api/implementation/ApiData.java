package team.floracore.common.api.implementation;

import org.floracore.api.data.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public class ApiData implements DataAPI {
    private final FloraCorePlugin plugin;

    public ApiData(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public Data getSpecifiedData(UUID uuid, DataType type, String key) {
        return plugin.getStorage().getImplementation().getSpecifiedData(uuid, type, key);
    }

    @Override
    public String getSpecifiedDataValue(UUID uuid, DataType type, String key) {
        return getSpecifiedData(uuid, type, key).getValue();
    }

    @Override
    public long getSpecifiedDataExpiry(UUID uuid, DataType type, String key) {
        return getSpecifiedData(uuid, type, key).getExpiry();
    }

    @Override
    public int getSpecifiedDataID(UUID uuid, DataType type, String key) {
        return getSpecifiedData(uuid, type, key).getId();
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
