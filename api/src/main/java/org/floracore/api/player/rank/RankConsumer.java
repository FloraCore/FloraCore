package org.floracore.api.player.rank;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 该接口定义了设置和重置指定玩家Rank的方法。
 *
 * <p>该接口允许设置和重置玩家的Rank。
 * <p>通过传递玩家的UUID和要设置的等级，可以将指定等级应用于指定玩家。同样，也可以重置指定玩家的Rank。
 *
 * @author xLikeWATCHDOG
 */
public interface RankConsumer {
    /**
     * 设置指定玩家的Rank。
     *
     * @param uuid 玩家的UUID。
     * @param rank 要设置的Rank。
     * @return 一个CompletableFuture对象，表示设置等级的异步操作。
     */
    CompletableFuture<Void> setRank(UUID uuid, String rank);

    /**
     * 重置指定玩家的Rank。
     *
     * @param uuid 玩家的UUID。
     * @return 一个CompletableFuture对象，表示重置等级的异步操作。
     */
    CompletableFuture<Void> resetRank(UUID uuid);
}
