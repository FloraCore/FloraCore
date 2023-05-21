package team.floracore.paper.command;

import org.floracore.api.server.*;
import team.floracore.common.storage.implementation.*;

import java.util.*;
import java.util.concurrent.*;

public interface FloraCoreCommand {
    boolean whetherServerEnableAutoSync1();

    boolean whetherServerEnableAutoSync2();

    ServerType getServerType();

    StorageImplementation getStorageImplementation();

    Executor getAsyncExecutor();

    CompletableFuture<Boolean> hasPermissionAsync(UUID uuid, String permission);

    boolean hasPermission(UUID uuid, String permission);

    String getPlayerRecordName(UUID uuid);

    boolean isOnline(UUID uuid);
}
