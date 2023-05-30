package team.floracore.common.command;

import team.floracore.common.storage.implementation.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * FloraCoreCommand 接口定义了一些核心命令的方法。
 */
public interface FloraCoreCommand {
    /**
     * 获取数据库存储实现。
     *
     * @return 数据库存储实现的对象。
     */
    StorageImplementation getStorageImplementation();

    /**
     * 获取异步执行器。
     *
     * @return 异步执行器的对象。
     */
    Executor getAsyncExecutor();

    /**
     * 检查指定 UUID 是否具有指定权限。
     *
     * @param uuid       要检查权限的 UUID。
     * @param permission 要检查的权限字符串。
     * @return 如果具有权限，则返回 true；否则返回 false。
     */
    boolean hasPermission(UUID uuid, String permission);

    /**
     * 获取指定 UUID 的玩家记录名称。
     *
     * @param uuid 要获取玩家记录名称的 UUID。
     * @return 玩家记录名称的字符串。
     */
    String getPlayerRecordName(UUID uuid);

    /**
     * 检查指定 UUID 的玩家是否在线。
     *
     * @param uuid 要检查的玩家 UUID。
     * @return 如果玩家在线，则返回 true；否则返回 false。
     */
    boolean isOnline(UUID uuid);
}
