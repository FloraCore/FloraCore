package team.floracore.common.api.implementation;

import com.github.benmanes.caffeine.cache.Cache;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.floracore.api.player.PermissionEvaluator;
import org.floracore.api.player.PlayerAPI;
import org.floracore.api.player.rank.RankConsumer;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.ONLINE;
import team.floracore.common.storage.misc.floracore.tables.PLAYER;
import team.floracore.common.util.CaffeineFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ApiPlayer implements PlayerAPI {
	private static final Cache<UUID, PLAYER> playersCache = CaffeineFactory.newBuilder()
			.expireAfterWrite(3, TimeUnit.SECONDS)
			.build();
	private static final Cache<String, PLAYER> playerRecordCache = CaffeineFactory.newBuilder()
			.expireAfterWrite(3, TimeUnit.SECONDS)
			.build();
	private final FloraCorePlugin plugin;
	private RankConsumer rankConsumer;
	private PermissionEvaluator permissionEvaluator;

	public ApiPlayer(FloraCorePlugin plugin) {
		this.plugin = plugin;
		this.permissionEvaluator = (uuid, permission) -> {
			if (plugin.luckPermsHook()) {
				// 在这里实现自定义的权限评估逻辑
				// 返回一个 CompletableFuture<Boolean> 对象
				LuckPerms luckPerms = LuckPermsProvider.get();
				CompletableFuture<User> future = luckPerms.getUserManager().loadUser(uuid);
				return future.thenApply(user -> {
					if (user == null) {
						// 加载用户数据失败
						return false;
					}
					return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
				});
			}
			Sender sender = plugin.getSender(uuid);
			if (sender == null) {
				return CompletableFuture.completedFuture(false);
			}
			return CompletableFuture.completedFuture(sender.hasPermission(permission));
		};
		try {
			this.rankConsumer = new RankConsumer() {
				@Override
				public CompletableFuture<Void> setRank(UUID uuid, String rank) {
					CompletableFuture<Void> future = new CompletableFuture<>();
					try {
						if (plugin.luckPermsHook()) {
							LuckPerms luckPerms = LuckPermsProvider.get();
							User user = luckPerms.getUserManager().getUser(uuid);
							if (user != null) {
								PrefixNode prefixNode = PrefixNode.builder(rank, Integer.MAX_VALUE).build();
								user.data().add(prefixNode);
								SuffixNode suffixNode = SuffixNode.builder("", Integer.MAX_VALUE).build();
								user.data().add(suffixNode);
								luckPerms.getUserManager().saveUser(user);
							}
						}
					} catch (Throwable ignored) {
					}
					future.complete(null);
					return future;
				}

				@Override
				public CompletableFuture<Void> resetRank(UUID uuid) {
					CompletableFuture<Void> future = new CompletableFuture<>();
					try {
						if (plugin.luckPermsHook()) {
							LuckPerms luckPerms = LuckPermsProvider.get();
							User user = luckPerms.getUserManager().getUser(uuid);
							if (user != null) {
								user.data().clear(node -> {
									if (node instanceof PrefixNode) {
										return ((PrefixNode) node).getPriority() == Integer.MAX_VALUE;
									} else if (node instanceof SuffixNode) {
										return ((SuffixNode) node).getPriority() == Integer.MAX_VALUE;
									}
									return false;
								});
								luckPerms.getUserManager().saveUser(user);
							}
						}
					} catch (Throwable ignored) {
					}
					future.complete(null);
					return future;
				}

				@Override
				public CompletableFuture<String> getPrefix(UUID uuid) {
					CompletableFuture<String> future = new CompletableFuture<>();
					try {
						if (plugin.luckPermsHook()) {
							LuckPerms luckPerms = LuckPermsProvider.get();
							User user = luckPerms.getUserManager().getUser(uuid);
							if (user != null) {
								Collection<PrefixNode> prefixNodes = user.getNodes(NodeType.PREFIX);
								PrefixNode selectedPrefix = getSelectedPrefix(prefixNodes);
								String prefix;
								if (selectedPrefix != null) {
									prefix = selectedPrefix.getMetaValue();
								} else {
									prefix = user.getCachedData().getMetaData().getPrefix();
								}
								future.complete(prefix);
							}
						}
					} catch (Throwable ignored) {
					}
					return future;
				}

				@Override
				public CompletableFuture<String> getSuffix(UUID uuid) {
					CompletableFuture<String> future = new CompletableFuture<>();
					try {
						if (plugin.luckPermsHook()) {
							LuckPerms luckPerms = LuckPermsProvider.get();
							User user = luckPerms.getUserManager().getUser(uuid);
							if (user != null) {
								Collection<SuffixNode> suffixNodes = user.getNodes(NodeType.SUFFIX);
								SuffixNode selectedSuffix = getSelectedSuffix(suffixNodes);
								String suffix;
								if (selectedSuffix != null) {
									suffix = selectedSuffix.getMetaValue();
								} else {
									suffix = user.getCachedData().getMetaData().getSuffix();
								}
								future.complete(suffix);
							}
						}
					} catch (Throwable ignored) {
					}
					return future;
				}
			};
		} catch (Throwable i) {
			this.rankConsumer = null;
		}
	}

	private static PrefixNode getSelectedPrefix(Collection<PrefixNode> prefixNodes) {
		int maxPriority = Integer.MIN_VALUE;
		int secondMaxPriority = Integer.MIN_VALUE;
		boolean hasMaxPriority = false;

		for (PrefixNode node : prefixNodes) {
			int priority = node.getPriority();

			if (priority == Integer.MAX_VALUE) {
				hasMaxPriority = true;
			}

			if (priority > maxPriority) {
				secondMaxPriority = maxPriority;
				maxPriority = priority;
			} else if (priority > secondMaxPriority && priority != maxPriority) {
				secondMaxPriority = priority;
			}
		}

		if (hasMaxPriority) {
			return getPrefixNodeByPriority(prefixNodes, secondMaxPriority);
		} else {
			return getPrefixNodeByPriority(prefixNodes, maxPriority);
		}
	}

	private static PrefixNode getPrefixNodeByPriority(Collection<PrefixNode> prefixNodes, int priority) {
		for (PrefixNode node : prefixNodes) {
			if (node.getPriority() == priority) {
				return node;
			}
		}
		return null;
	}

	private static SuffixNode getSelectedSuffix(Collection<SuffixNode> suffixNodes) {
		int maxPriority = Integer.MIN_VALUE;
		int secondMaxPriority = Integer.MIN_VALUE;
		boolean hasMaxPriority = false;

		for (SuffixNode node : suffixNodes) {
			int priority = node.getPriority();

			if (priority == Integer.MAX_VALUE) {
				hasMaxPriority = true;
			}

			if (priority > maxPriority) {
				secondMaxPriority = maxPriority;
				maxPriority = priority;
			} else if (priority > secondMaxPriority && priority != maxPriority) {
				secondMaxPriority = priority;
			}
		}

		if (hasMaxPriority) {
			return getSuffixNodeByPriority(suffixNodes, secondMaxPriority);
		} else {
			return getSuffixNodeByPriority(suffixNodes, maxPriority);
		}
	}

	private static SuffixNode getSuffixNodeByPriority(Collection<SuffixNode> suffixNodes, int priority) {
		for (SuffixNode node : suffixNodes) {
			if (node.getPriority() == priority) {
				return node;
			}
		}
		return null;
	}


	@Override
	public boolean hasPlayerRecord(String name) {
		return getPlayer(name) != null;
	}

	public PLAYER getPlayer(String name) {
		PLAYER player = playerRecordCache.getIfPresent(name);
		if (player == null) {
			player = plugin.getStorage().getImplementation().selectPlayer(name);
			if (player == null) {
				return null;
			}
			playerRecordCache.put(name, player);
		}
		return player;
	}

	@Override
	public UUID getPlayerRecordUUID(String name) {
		PLAYER players = getPlayer(name);
		if (players == null) {
			return null;
		}
		return players.getUniqueId();
	}

	@Override
	public String getPlayerRecordName(UUID uuid) {
		PLAYER players = getPlayer(uuid);
		if (players == null) {
			return null;
		}
		return players.getName();
	}

	public PLAYER getPlayer(UUID uuid) {
		PLAYER player = playersCache.getIfPresent(uuid);
		if (player == null) {
			player = plugin.getStorage().getImplementation().selectPlayer(uuid);
			if (player == null) {
				return null;
			}
			playersCache.put(uuid, player);
		}
		return player;
	}

	@Override
	public boolean isOnline(UUID uuid) {
		List<Sender> senders = plugin.getOnlineSenders().collect(Collectors.toList());
		for (Sender sender : senders) {
			if (sender.getUniqueId() == uuid) {
				return true;
			}
		}
		ONLINE online = plugin.getStorage().getImplementation().selectOnline(uuid);
		if (online == null) {
			return false;
		}
		return online.getStatus();
	}

	public FloraCorePlugin getPlugin() {
		return plugin;
	}

	@Override
	public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
		this.permissionEvaluator = permissionEvaluator;
	}

	@Override
	public CompletableFuture<Boolean> hasPermissionAsync(UUID uuid, String permission, PermissionEvaluator evaluator) {
		return evaluator.evaluate(uuid, permission);
	}

	@Override
	public boolean hasPermission(UUID uuid, String permission) {
		return hasPermissionAsync(uuid, permission, this.permissionEvaluator).join();
	}

	@Override
	public CompletableFuture<Void> setRank(UUID uuid, String rank, RankConsumer rankConsumer) {
		return rankConsumer.setRank(uuid, rank);
	}

	@Override
	public void setRank(UUID uuid, String rank) throws NullPointerException {
		setRank(uuid, rank, this.rankConsumer).join();
	}

	@Override
	public CompletableFuture<Void> resetRank(UUID uuid, RankConsumer rankConsumer) throws NullPointerException {
		if (rankConsumer == null) {
			throw new NullPointerException();
		}
		return rankConsumer.resetRank(uuid);
	}

	@Override
	public void resetRank(UUID uuid) throws NullPointerException {
		resetRank(uuid, this.rankConsumer).join();
	}

	@Override
	public void setRankConsumer(RankConsumer rankConsumer) throws NullPointerException {
		if (rankConsumer == null) {
			throw new NullPointerException();
		}
		this.rankConsumer = rankConsumer;
	}

	@Override
	public String getPrefix(UUID uuid) {
		return getPrefix(uuid, this.rankConsumer);
	}

	@Override
	public String getPrefix(UUID uuid, RankConsumer rankConsumer) {
		if (rankConsumer == null) {
			throw new NullPointerException();
		}
		return rankConsumer.getPrefix(uuid).join();
	}

	@Override
	public String getSuffix(UUID uuid) {
		return getSuffix(uuid, this.rankConsumer);
	}

	@Override
	public String getSuffix(UUID uuid, RankConsumer rankConsumer) {
		if (rankConsumer == null) {
			throw new NullPointerException();
		}
		return rankConsumer.getSuffix(uuid).join();
	}
}
