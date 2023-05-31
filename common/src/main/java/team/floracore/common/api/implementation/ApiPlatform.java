package team.floracore.common.api.implementation;

import org.floracore.api.platform.Platform;
import org.floracore.api.platform.PluginMetadata;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.plugin.FloraCorePlugin;

import java.time.Instant;

public class ApiPlatform implements Platform, PluginMetadata {
    private final FloraCorePlugin plugin;

    public ApiPlatform(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getBootstrap().getVersion();
    }

    @Override
    public @NotNull String getApiVersion() {
        String[] version = this.plugin.getBootstrap().getVersion().split("\\.");
        return version[0] + '.' + version[1];
    }

    @Override
    public Platform.@NotNull Type getType() {
        return this.plugin.getBootstrap().getType();
    }

    @Override
    public @NotNull Instant getStartTime() {
        return this.plugin.getBootstrap().getStartupTime();
    }
}
