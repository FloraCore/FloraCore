package org.floracore.api;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import org.floracore.api.platform.*;
import org.floracore.api.player.*;

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

    /**
     * Gets the {@link Platform}, which represents the server platform the
     * plugin is running on.
     *
     * @return the platform
     */
    @NonNull Platform getPlatform();

    /**
     * Gets the {@link PluginMetadata}, responsible for providing metadata about
     * the FloraCore plugin currently running.
     *
     * @return the plugin metadata
     */
    @NonNull PluginMetadata getPluginMetadata();
}
