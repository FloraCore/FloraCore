package org.floracore.api.messenger.message;

import org.floracore.api.messenger.IncomingMessageConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an outgoing {@link Message}.
 *
 * <p>Outgoing messages are ones which have been generated by this instance.
 * (in other words, they are implemented by FloraCore)</p>
 *
 * <p>Note that all implementations of this interface are guaranteed to be an
 * instance of one of the interfaces extending {@link Message} in the
 * 'api.messenger.message.type' package.</p>
 */
public interface OutgoingMessage extends Message {

    /**
     * Gets an encoded string form of this message.
     *
     * <p>The format of this string is likely to change between versions and
     * should not be depended on.</p>
     *
     * <p>Implementations which want to use a standard method of serialisation
     * can send outgoing messages using the string returned by this method, and
     * pass on the message on the "other side" using
     * {@link IncomingMessageConsumer#consumeIncomingMessageAsString(String)}.</p>
     *
     * @return an encoded string form of the message
     */
    @NotNull
    String asEncodedString();

}
