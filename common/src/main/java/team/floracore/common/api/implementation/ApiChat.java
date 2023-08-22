package team.floracore.common.api.implementation;

import lombok.Getter;
import org.floracore.api.data.chat.ChatAPI;
import team.floracore.common.plugin.FloraCorePlugin;

@Getter
public class ApiChat implements ChatAPI {
	private final FloraCorePlugin plugin;

	public ApiChat(FloraCorePlugin plugin) {
		this.plugin = plugin;
	}

}
