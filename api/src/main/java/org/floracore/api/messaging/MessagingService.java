package org.floracore.api.messaging;

import org.checkerframework.checker.nullness.qual.*;

/**
 * A means to push changes to other servers using the platforms networking
 */
public interface MessagingService {

    /**
     * Gets the name of this messaging service
     *
     * @return the name of this messaging service
     */
    @NonNull String getName();

}
