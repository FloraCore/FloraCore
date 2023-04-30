package org.floracore.api;

import org.floracore.api.chat.*;
import org.floracore.api.data.*;
import org.floracore.api.player.*;
import org.floracore.api.server.*;

/**
 * The FloraCore API.
 *
 * <p>The API allows other plugins on the server to read and modify FloraCore
 * data, change behaviour of the plugin, listen to certain events, and integrate
 * FloraCore into other plugins and systems.</p>
 *
 * <p>This interface represents the base of the API package. All functions are
 * accessed via this interface.</p>
 *
 * <p>To start using the API, you need to obtain an instance of this interface.
 * These are registered by the FloraCore plugin to the platforms Services
 * Manager. This is the preferred method for obtaining an instance.</p>
 *
 * <p>For ease of use, and for platforms without a Service Manager, an instance
 * can also be obtained from the static singleton accessor in
 * {@link FloraCoreProvider}.</p>
 */
public interface FloraCore {

    /**
     * Gets the name of this server.
     *
     * <p>This is defined in the FloraCore configuration file, and is used for
     * server specific permission handling.</p>
     *
     * <p>The default server name is "global".</p>
     *
     * @return the server name
     */
    String getServerName();

    /**
     * 获取服务器的类型
     *
     * @return 服务器的类型
     */
    ServerType getServerType();

    /**
     * 获取数据API
     *
     * @return 数据API
     */
    DataAPI getDataAPI();

    /**
     * 获取玩家API
     *
     * @return 玩家API
     */
    PlayerAPI getPlayerAPI();

    /**
     * 获取聊天API
     *
     * @return 聊天API
     */
    ChatAPI getChatAPI();
}
