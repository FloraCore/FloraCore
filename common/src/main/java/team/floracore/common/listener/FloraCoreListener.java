package team.floracore.common.listener;

import java.util.concurrent.Executor;

public interface FloraCoreListener {
	Executor getAsyncExecutor();
}
