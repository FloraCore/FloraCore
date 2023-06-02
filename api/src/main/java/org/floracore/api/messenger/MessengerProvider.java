package org.floracore.api.messenger;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a provider for {@link Messenger} instances.
 *
 * <p>Users wishing to provide their own implementation for the plugins
 * "Messaging Service" should implement and register this interface.</p>
 *
 * @see org.floracore.api.FloraCore#registerMessengerProvider(MessengerProvider)
 */
@OverrideOnly
public interface MessengerProvider {

    /**
     * Gets the name of this provider.
     *
     * @return the provider name
     */
    @NotNull
    String getName();

    /**
     * Creates and returns a new {@link Messenger} instance, which passes
     * incoming messages to the provided {@link IncomingMessageConsumer}.
     *
     * <p>As the agent should pass incoming messages to the given consumer,
     * this method should always return a new object.</p>
     *
     * @param incomingMessageConsumer the consumer the new instance should pass
     *                                incoming messages to
     * @return a new messenger agent instance
     */
    @NotNull
    Messenger obtain(@NotNull IncomingMessageConsumer incomingMessageConsumer);

}
