/**
 * 数据库操作的接口
 */
package team.floracore.common.storage.implementation;

import team.floracore.common.plugin.*;

public interface StorageImplementation {
    FloraCorePlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    void shutdown();
}
