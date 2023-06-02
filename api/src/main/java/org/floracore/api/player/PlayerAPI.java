package org.floracore.api.player;

import org.floracore.api.player.rank.RankConsumer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 玩家API。
 */
public interface PlayerAPI {
    /**
     * 获取服务器内是否有这名玩家的记录。
     * 判断逻辑是通过name查询这名玩家是否上线过。
     * 可能出现的问题是,无法判定这名玩家之前的昵称。
     *
     * @param name 玩家昵称
     * @return 是否记录状况
     */
    boolean hasPlayerRecord(String name);

    /**
     * 获取服务器内这名玩家记录的UUID值。
     * 该值通常情况下不会变。
     * 若不存在,则返回null。
     *
     * @param name 玩家昵称
     * @return 玩家UUID
     */
    UUID getPlayerRecordUUID(String name);

    /**
     * 通过数据库获取玩家最后记录的名字。
     *
     * @param uuid 玩家的UUID
     * @return 最后记录的名字, 若无返回null
     */
    String getPlayerRecordName(UUID uuid);

    /**
     * 判断玩家是否在线。
     * 该逻辑首先判断玩家是否在当前服务器中在线。
     * 如果不在,再通过数据库获取。
     *
     * @param uuid 玩家的UUID
     * @return 在线情况
     */
    boolean isOnline(UUID uuid);

    /**
     * 设置权限评估器。
     *
     * @param permissionEvaluator 权限评估器，用于评估权限。
     */
    void setPermissionEvaluator(PermissionEvaluator permissionEvaluator);

    /**
     * 异步检查指定 UUID 是否具有指定权限。
     *
     * @param uuid       要检查权限的 UUID。
     * @param permission 要检查的权限字符串。
     * @param evaluator  权限评估器，用于评估权限。
     * @return 一个 CompletableFuture 对象，表示权限检查的结果。返回值为布尔类型。如果具有权限，则返回 true；否则返回 false。
     */
    CompletableFuture<Boolean> hasPermissionAsync(UUID uuid, String permission, PermissionEvaluator evaluator);

    /**
     * 同步检查指定 UUID的玩家 是否具有指定权限。
     *
     * @param uuid       要检查权限的 UUID。
     * @param permission 要检查的权限字符串。
     * @return 如果具有权限，则返回 true；否则返回 false。
     */
    boolean hasPermission(UUID uuid, String permission);

    /**
     * 设置指定玩家的Rank。
     *
     * @param uuid         玩家的UUID。
     * @param rank         要设置的Rank。
     * @param rankConsumer Rank处理者对象，用于在设置Rank时执行自定义逻辑。
     */
    CompletableFuture<Void> setRank(UUID uuid, String rank, RankConsumer rankConsumer) throws NullPointerException;

    /**
     * 设置指定玩家的Rank。
     *
     * @param uuid 玩家的UUID。
     * @param rank 要设置的Rank。
     */
    void setRank(UUID uuid, String rank) throws NullPointerException;

    /**
     * 重置指定玩家的Rank。
     *
     * @param uuid         玩家的UUID。
     * @param rankConsumer Rank处理者对象，用于在重置Rank时执行自定义逻辑。
     */
    CompletableFuture<Void> resetRank(UUID uuid, RankConsumer rankConsumer) throws NullPointerException;

    /**
     * 重置指定玩家的Rank。
     *
     * @param uuid 玩家的UUID。
     */
    void resetRank(UUID uuid) throws NullPointerException;

    void setRankConsumer(RankConsumer rankConsumer);
}
