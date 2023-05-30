package team.floracore.common.listener;

import java.util.concurrent.*;

public interface FloraCoreListener {
    Executor getAsyncExecutor();
}
