package org.floracore.api.messenger.message;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.*;
import org.jetbrains.annotations.ApiStatus.*;

import java.util.*;

/**
 * Represents a message sent received via a {@link Messenger}.
 */
@NonExtendable
public interface Message {

    /**
     * Gets the unique id associated with this message.
     *
     * <p>This ID is used to ensure a single server instance doesn't process
     * the same message twice.</p>
     *
     * @return the id of the message
     */
    @NonNull UUID getId();

}
