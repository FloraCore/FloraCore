package org.floracore.api.messenger.message;

import org.floracore.api.messenger.Messenger;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
	@NotNull
	UUID getId();

}
