package team.floracore.api;

import team.floracore.api.data.*;
import team.floracore.api.player.*;

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
 * {@link team.floracore.api.FloraCoreProvider}.</p>
 */
public interface FloraCore {
    DataAPI getDataAPI();

    PlayerAPI getPlayerAPI();
}
