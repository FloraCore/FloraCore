package org.floracore.api.player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * {@code PermissionEvaluator} 接口代表一个权限评估器，用于对给定的 UUID 和权限字符串进行权限评估。
 *
 * @author xLikeWATCHDOG
 */
public interface PermissionEvaluator {
	/**
	 * 评估给定 UUID 是否具有指定权限。
	 *
	 * @param uuid       要评估权限的 UUID
	 * @param permission 要评估的权限字符串
	 *
	 * @return 一个 CompletableFuture 对象，表示权限评估的结果，返回值为布尔类型。如果具有权限，则返回 true；否则返回 false。
	 */
	CompletableFuture<Boolean> evaluate(UUID uuid, String permission);
}
