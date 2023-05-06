package team.floracore.common.api.implementation;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messaging.*;
import team.floracore.common.messaging.*;

public class ApiMessagingService implements MessagingService {
    private final InternalMessagingService handle;

    public ApiMessagingService(InternalMessagingService handle) {
        this.handle = handle;
    }

    @Override
    public @NonNull String getName() {
        return this.handle.getName();
    }
}
