package team.floracore.common.api.implementation;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.platform.*;
import team.floracore.common.plugin.*;

import java.time.*;

public class ApiPlatform implements Platform, PluginMetadata {
    private final FloraCorePlugin plugin;

    public ApiPlatform(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NonNull String getVersion() {
        return this.plugin.getBootstrap().getVersion();
    }

    @Override
    public @NonNull String getApiVersion() {
        String[] version = this.plugin.getBootstrap().getVersion().split("\\.");
        return version[0] + '.' + version[1];
    }

    @Override
    public Platform.@NonNull Type getType() {
        return this.plugin.getBootstrap().getType();
    }

    @Override
    public @NonNull Instant getStartTime() {
        return this.plugin.getBootstrap().getStartupTime();
    }
}
