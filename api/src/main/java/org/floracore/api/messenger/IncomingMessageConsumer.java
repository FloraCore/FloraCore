package org.floracore.api.messenger;

import org.floracore.api.messenger.message.*;
import org.jetbrains.annotations.ApiStatus.*;
import org.jetbrains.annotations.*;

/**
 * Encapsulates the FloraCore system which accepts incoming {@link Message}s
 * from implementations of {@link Messenger}.
 */
@NonExtendable
public interface IncomingMessageConsumer {
    /**
     * Consumes a message in an encoded string format.
     *
     * <p>This method will decode strings obtained by calling
     * {@link OutgoingMessage#asEncodedString()}. This means that basic
     * implementations can successfully implement {@link Messenger} without
     * providing their own serialisation.</p>
     *
     * <p>The boolean returned from this method indicates whether or not the
     * platform accepted the message. Some implementations which have multiple
     * distribution channels may wish to use this result to dispatch the same
     * message back to additional receivers.</p>
     *
     * <p>The implementation will usually return <code>false</code> if a message
     * with the same ping id has already been processed.</p>
     *
     * @param encodedString the encoded string
     * @return true if the plugin accepted the message
     */
    boolean consumeIncomingMessageAsString(@NotNull String encodedString);

}
