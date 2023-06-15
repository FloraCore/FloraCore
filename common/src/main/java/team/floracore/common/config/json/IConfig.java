package team.floracore.common.config.json;

import java.io.IOException;

/**
 * @author xLikeWATCHDOG
 */
public interface IConfig<T> {
    void autoCreateNewFile() throws IOException;

    /**
     * 加载JSON
     *
     * @throws IOException 异常
     */
    void loadConfig() throws IOException;

    /**
     * 保存JSON
     *
     * @throws IOException 异常
     */
    void saveConfig() throws IOException;

    T getConfig();
}