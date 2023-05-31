package org.floracore.api.messenger;

import org.floracore.api.messenger.message.Message;
import org.floracore.api.messenger.message.OutgoingMessage;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object which dispatches {@link OutgoingMessage}s.
 */
@OverrideOnly
public interface Messenger extends AutoCloseable {

    /**
     * Performs the necessary action to dispatch the message using the means
     * of the messenger.
     *
     * <p>The outgoing message instance is guaranteed to be an instance of one
     * of the interfaces extending {@link Message} in the
     * 'api.messenger.message.type' package.</p>
     *
     * <p>3rd party implementations are encouraged to implement this method with consideration
     * that new types may be added in the future.</p>
     *
     * <p>This call is always made async.</p>
     *
     * @param outgoingMessage the outgoing message
     */
    void sendOutgoingMessage(@NotNull OutgoingMessage outgoingMessage);

    /**
     * Performs the necessary action to gracefully shut down the messenger.
     */
    @Override
    default void close() {

    }
}
