package team.floracore.common.command;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.util.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractFloraCoreCommand implements FloraCoreCommand {
    public final static String EMPTY_DESCRIPTION = "%empty%";
    private final FloraCorePlugin plugin;

    public AbstractFloraCoreCommand(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public static Duration parseDuration(String input) {
        try {
            long number = Long.parseLong(input);
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            return checkPastDate(Duration.between(now, Instant.ofEpochSecond(number)));
        } catch (NumberFormatException e) {
            // ignore
        }

        try {
            return checkPastDate(DurationParser.parseDuration(input));
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

    private static Duration checkPastDate(Duration duration) {
        if (duration.isNegative()) {
            throw new RuntimeException();
        }
        return duration;
    }

    @Override
    public StorageImplementation getStorageImplementation() {
        return plugin.getStorage().getImplementation();
    }

    @Override
    public Executor getAsyncExecutor() {
        return plugin.getBootstrap().getScheduler().async();
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return plugin.getApiProvider().getPlayerAPI().hasPermission(uuid, permission);
    }

    @Override
    public String getPlayerRecordName(UUID uuid) {
        return plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return plugin.getApiProvider().getPlayerAPI().isOnline(uuid);
    }
}
