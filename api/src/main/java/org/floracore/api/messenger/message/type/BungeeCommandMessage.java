package org.floracore.api.messenger.message.type;

import org.floracore.api.messenger.message.Message;
import org.jetbrains.annotations.NotNull;

/**
 * @author xLikeWATCHDOG
 */
public interface BungeeCommandMessage extends Message {
	/**
	 * 在BungeeCord上运行的命令
	 *
	 * @return 在BungeeCord上运行的命令
	 */
	@NotNull String getCommand();
}
