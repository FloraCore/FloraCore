package team.floracore.common.command;

import team.floracore.common.storage.implementation.*;

import java.util.*;
import java.util.concurrent.*;

public interface FloraCoreCommand {
    StorageImplementation getStorageImplementation();

    Executor getAsyncExecutor();

    CompletableFuture<Boolean> hasPermissionAsync(UUID uuid, String permission);

    boolean hasPermission(UUID uuid, String permission);

    String getPlayerRecordName(UUID uuid);

    boolean isOnline(UUID uuid);
}
