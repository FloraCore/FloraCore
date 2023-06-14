package team.floracore.api.data;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.SortedMap;
import java.util.UUID;

/**
 * 这个类是关于Data数据库的API。
 */
public interface DataAPI {
    /**
     * 获取指定的Data的值。
     * 若数据不存在,则返回null。
     *
     * @param uuid 玩家的UUID
     * @param type 数据类型
     * @param key  数据值的键值
     * @return 数据值
     */
    @Nullable String getSpecifiedDataValue(UUID uuid, DataType type, String key);

    Integer getSpecifiedDataIntValue(UUID uuid, DataType type, String key);

    /**
     * 获取指定Data的过期时间戳。
     * 若数据不存在,则返回null。
     *
     * @param uuid 玩家的UUID
     * @param type 数据类型
     * @param key  数据值的键值
     * @return 过期时间戳
     */
    @Nullable Long getSpecifiedDataExpiry(UUID uuid, DataType type, String key);

    Long getSpecifiedDataIntExpiry(UUID uuid, DataType type, String key);

    /**
     * 获取指定Data的ID
     * 若数据不存在,则返回null。
     *
     * @param uuid 玩家的UUID
     * @param type 数据类型
     * @param key  数据值的键值
     * @return ID
     */
    @Nullable Integer getSpecifiedDataID(UUID uuid, DataType type, String key);

    Integer getSpecifiedDataIntID(UUID uuid, DataType type, String key);

    /**
     * 插入数据
     * 虽然名称是插入数据,但是并不是简单的插入数据。
     * 如果不存在数据,才会进行插入操作。
     * 如果存在数据,则会读取数据,而不进行插入操作。
     * 如果存在数据,还会同时更新value和expiry数据。
     * 返回为-1即不存在数据的标志。
     *
     * @param uuid   玩家的UUID
     * @param type   数据类型
     * @param key    数据值的键值
     * @param value  数据值
     * @param expiry 过期时间戳
     * @return ID, 若原先不存在数据, 则返回-1,并插入数据库中。
     */
    int insertData(UUID uuid, DataType type, String key, String value, long expiry);

    int insertDataInt(UUID uuid, DataType type, String key, int value, long expiry);

    /**
     * 根据ID删除数据
     *
     * @param id ID
     */
    void deleteData(int id);

    void deleteDataInt(int id);

    /**
     * 删除一名玩家全部的数据
     *
     * @param uuid 玩家的UUID
     */
    void deleteDataAll(UUID uuid);

    void deleteDataIntAll(UUID uuid);

    /**
     * 删除一名玩家关于type的全部数据
     *
     * @param uuid 玩家的UUID
     * @param type 数据类型
     */
    void deleteDataType(UUID uuid, DataType type);

    void deleteDataIntType(UUID uuid, DataType type);

    /**
     * 删除一名玩家已经给过期的全部数据
     *
     * @param uuid 玩家的UUID
     */
    void deleteDataExpired(UUID uuid);

    void deleteDataIntExpired(UUID uuid);

    SortedMap<UUID, Integer> getDataIntSorted(DataType dataType, String key, boolean ascending);
}
